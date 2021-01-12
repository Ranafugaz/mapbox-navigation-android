package com.mapbox.navigation.ui.maps.camera

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import androidx.annotation.UiThread
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.animator.CameraAnimator
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.data.ViewportData
import com.mapbox.navigation.ui.maps.camera.data.ViewportDataSource
import com.mapbox.navigation.ui.maps.camera.data.ViewportDataSourceUpdateObserver
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState.FOLLOWING
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState.IDLE
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState.OVERVIEW
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState.TRANSITION_TO_FOLLOWING
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState.TRANSITION_TO_OVERVIEW
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraStateChangedObserver
import com.mapbox.navigation.ui.maps.camera.transition.MapboxNavigationCameraStateTransition
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraStateTransition
import java.util.concurrent.CopyOnWriteArraySet

/**
 * `NavigationCamera` is a class that tries to simplify management of the Map's camera object in
 * typical navigation scenarios. It's fed camera frames via the [ViewportDataSource],
 * generates transitions with [NavigationCameraStateTransition] and executes them.
 *
 * `NavigationCamera`'s lifecycle can't exceed the lifecycle of
 * the [MapboxMap] (or indirectly [MapView]) that it's attached to without risking reference leaks.
 *
 * ## States
 * The `NavigationCamera` is an entity that offers to maintain 3 distinct [NavigationCameraState]s:
 * [IDLE], [FOLLOWING], and [OVERVIEW]. States can be requested at any point in runtime.
 *
 * When [FOLLOWING] or [OVERVIEW] states are engaged, the `NavigationCamera` assumes full ownership
 * of the [CameraAnimationsPlugin]. This means that if any other camera transition is scheduled
 * outside of the `NavigationCamera`’s context, there might be side-effects or glitches.
 * Consequently, if you want to perform other camera transitions,
 * first call [requestNavigationCameraToIdle], and only after that perform the desired transition.
 *
 * When the camera is transitioning between states, it reports that status with
 * [TRANSITION_TO_FOLLOWING] and [TRANSITION_TO_OVERVIEW] helper states.
 * These helper transition states cannot be directly requested.
 *
 * Change to [IDLE] state is always instantaneous.
 *
 * ## Data
 * In order to be able to perform state transitions or later frame updates,
 * the `NavigationCamera` needs data. This is provided by the [ViewportDataSource] argument.
 * The source is an observable interface that produces `CameraOptions` that frame the camera
 * for both [FOLLOWING] and [OVERVIEW] states.
 *
 * On creation, `NavigationCamera` subscribes to the data source and listens for updates.
 *
 * [MapboxNavigationViewportDataSource] is a default implementation of the source that helps to
 * generate camera frames based on the current route’s geometry, road's graph, trip's progress, etc.
 *
 * ## Transitions
 * When `NavigationCamera` is supplied with data and a state request, it invokes the
 * [NavigationCameraStateTransition] that generates a set of Map SDK [CameraAnimator]s that perform
 * the transition to the desired camera position created by the data source.
 *
 * When a state is requested, `NavigationCamera` takes the latest computed [ViewportData] values
 * and passes them to the [NavigationCameraStateTransition]
 * to create the [NavigationCameraStateTransition.transitionToFollowing]
 * or [NavigationCameraStateTransition.transitionToOverview] transitions.
 *
 * When `NavigationCamera` already is in one of the [FOLLOWING] or [OVERVIEW] states,
 * data source updates trigger creation of [NavigationCameraStateTransition.updateFrameForFollowing]
 * or [NavigationCameraStateTransition.updateFrameForOverview] transitions.
 *
 * After generating the transitions, `NavigationCamera` handles registering them to Maps SDK,
 * executing, listening for cancellation, adjusting states, etc.
 *
 * ## Gestures
 * When map is interacted with (whenever [OnMoveListener.onMoveBegin] fires), `NavigationCamera`
 * automatically jumps to [IDLE] state.
 */
@UiThread
class NavigationCamera(
    mapboxMap: MapboxMap,
    private val cameraPlugin: CameraAnimationsPlugin,
    gesturesPlugin: GesturesPlugin,
    private val viewportDataSource: ViewportDataSource,
    private val stateTransition: NavigationCameraStateTransition =
        MapboxNavigationCameraStateTransition(mapboxMap, cameraPlugin)
) {

    private var runningAnimation: AnimatorSet? = null

    private val navigationCameraStateChangedObservers =
        CopyOnWriteArraySet<NavigationCameraStateChangedObserver>()

    /**
     * Returns current [NavigationCameraState].
     * @see registerNavigationCameraStateChangeObserver
     */
    var state: NavigationCameraState = IDLE
        private set(value) {
            if (value != field) {
                field = value
                navigationCameraStateChangedObservers.forEach {
                    it.onNavigationCameraStateChanged(value)
                }
            }
        }

    private val sourceUpdateObserver = object : ViewportDataSourceUpdateObserver {
        override fun viewportDataSourceUpdated(viewportData: ViewportData) {
            updateFrame(viewportData, instant = false)
        }
    }

    private val moveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            requestNavigationCameraToIdle()
        }

        override fun onMove(detector: MoveGestureDetector) {
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
        }
    }

    init {
        viewportDataSource.registerUpdateObserver(sourceUpdateObserver)
        gesturesPlugin.addOnMoveListener(moveListener)
    }

    /**
     * Executes a transition to [FOLLOWING] state. When started, goes to [TRANSITION_TO_FOLLOWING]
     * and to the final [FOLLOWING] when ended. If transition is canceled, state goes to [IDLE].
     *
     * The target camera position is obtained with [ViewportDataSource.getViewportData].
     *
     * @param animatorListener invoked on transition's progress
     */
    fun requestNavigationCameraToFollowing(
        animatorListener: Animator.AnimatorListener? = null
    ) {
        when (state) {
            TRANSITION_TO_FOLLOWING, FOLLOWING -> {
                return
            }
            IDLE, TRANSITION_TO_OVERVIEW, OVERVIEW -> {
                val data = viewportDataSource.getViewportData()
                startAnimation(
                    stateTransition.transitionToFollowing(data.cameraForFollowing).apply {
                        addListener(
                            createTransitionListener(
                                TRANSITION_TO_FOLLOWING,
                                FOLLOWING,
                                animatorListener
                            )
                        )
                    },
                    instant = false
                )
            }
        }
    }

    /**
     * Executes a transition to [OVERVIEW] state. When started, goes to [TRANSITION_TO_OVERVIEW]
     * and to the final [OVERVIEW] when ended. If transition is canceled, state goes to [IDLE].
     *
     * The target camera position is obtained with [ViewportDataSource.getViewportData].
     *
     * @param animatorListener invoked on transition's progress
     */
    fun requestNavigationCameraToOverview(
        animatorListener: Animator.AnimatorListener? = null
    ) {
        when (state) {
            TRANSITION_TO_OVERVIEW, OVERVIEW -> {
                return
            }
            IDLE, TRANSITION_TO_FOLLOWING, FOLLOWING -> {
                val data = viewportDataSource.getViewportData()
                startAnimation(
                    stateTransition.transitionToOverview(data.cameraForOverview).apply {
                        addListener(
                            createTransitionListener(
                                TRANSITION_TO_OVERVIEW,
                                OVERVIEW,
                                animatorListener
                            )
                        )
                    },
                    instant = false
                )
            }
        }
    }

    /**
     * Immediately goes to [IDLE] state canceling all ongoing transitions.
     */
    fun requestNavigationCameraToIdle() {
        if (state != IDLE) {
            cancelAnimation()
            state = IDLE
        }
    }

    /**
     * If the [state] is [FOLLOWING] or [OVERVIEW],
     * performs an immediate camera transition (a jump, with animation duration equal to `0`)
     * based on the latest data obtained with [ViewportDataSource.getViewportData].
     */
    fun resetFrame() {
        val viewportData = viewportDataSource.getViewportData()
        updateFrame(viewportData, instant = true)
    }

    private fun updateFrame(viewportData: ViewportData, instant: Boolean) {
        when (state) {
            FOLLOWING -> {
                startAnimation(
                    stateTransition.updateFrameForFollowing(viewportData.cameraForFollowing).apply {
                        addListener(createFrameListener())
                    },
                    instant
                )
            }
            OVERVIEW -> {
                startAnimation(
                    stateTransition.updateFrameForOverview(viewportData.cameraForOverview).apply {
                        addListener(createFrameListener())
                    },
                    instant
                )
            }
            IDLE, TRANSITION_TO_FOLLOWING, TRANSITION_TO_OVERVIEW -> {
                // no impl
            }
        }
    }

    /**
     * Registers [NavigationCameraStateChangedObserver].
     */
    fun registerNavigationCameraStateChangeObserver(
        navigationCameraStateChangedObserver: NavigationCameraStateChangedObserver
    ) {
        navigationCameraStateChangedObservers.add(navigationCameraStateChangedObserver)
        navigationCameraStateChangedObserver.onNavigationCameraStateChanged(state)
    }

    /**
     * Unregisters [NavigationCameraStateChangedObserver].
     */
    fun unregisterNavigationCameraStateChangeObserver(
        navigationCameraStateChangedObserver: NavigationCameraStateChangedObserver
    ) {
        navigationCameraStateChangedObservers.remove(navigationCameraStateChangedObserver)
    }

    private fun cancelAnimation() {
        runningAnimation?.let { set ->
            set.cancel()
            set.childAnimations.forEach {
                cameraPlugin.unregisterAnimators(it as ValueAnimator)
            }
        }
        runningAnimation = null
    }

    private fun startAnimation(animatorSet: AnimatorSet, instant: Boolean) {
        cancelAnimation()
        animatorSet.childAnimations.forEach {
            cameraPlugin.registerAnimators(it as ValueAnimator)
        }
        if (instant) {
            animatorSet.duration = 0
        }
        animatorSet.start()
        runningAnimation = animatorSet
    }

    private fun finishAnimation(animatorSet: AnimatorSet) {
        animatorSet.childAnimations.forEach {
            cameraPlugin.unregisterAnimators(it as ValueAnimator)
        }
        if (runningAnimation == animatorSet) {
            runningAnimation = null
        }
    }

    private fun createTransitionListener(
        progressState: NavigationCameraState,
        finalState: NavigationCameraState,
        externalListener: Animator.AnimatorListener?
    ) = object : Animator.AnimatorListener {

        private var isCanceled = false

        override fun onAnimationStart(animation: Animator?) {
            state = progressState
            externalListener?.onAnimationStart(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            state = if (isCanceled) {
                IDLE
            } else {
                finalState
            }
            finishAnimation(animation as AnimatorSet)
            externalListener?.onAnimationEnd(animation)
            updateFrame(viewportDataSource.getViewportData(), instant = false)
        }

        override fun onAnimationCancel(animation: Animator?) {
            if (!isCanceled) {
                isCanceled = true
                externalListener?.onAnimationCancel(animation)
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {
            externalListener?.onAnimationRepeat(animation)
        }
    }

    private fun createFrameListener() = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator?) {
            // no impl
        }

        override fun onAnimationEnd(animation: Animator?) {
            finishAnimation(animation as AnimatorSet)
        }

        override fun onAnimationCancel(animation: Animator?) {
            // no impl
        }

        override fun onAnimationRepeat(animation: Animator?) {
            // no impl
        }
    }
}
