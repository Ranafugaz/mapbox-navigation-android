package com.mapbox.navigation.core.telemetry.events

import androidx.annotation.StringDef

/**
 * Scope of user's feedback events that might be sent through [com.mapbox.navigation.core.MapboxNavigation.postUserFeedback].
 *
 * This event occurs if the user taps a feedback button in the navigation app indicating there was a problem.
 */
object FeedbackEvent {

    /**
     * Feedback type *incorrect visual*
     */
    const val INCORRECT_VISUAL = "incorrect_visual"

    /**
     * Feedback type *road issue*
     */
    const val ROAD_ISSUE = "road_issue"

    /**
     * Feedback type *traffic issue*: wrong traffic
     */
    const val TRAFFIC_ISSUE = "traffic_issue"

    /**
     * Feedback type *other issue*: for feedback not categorized anywhere else
     */
    const val OTHER_ISSUE = "other_issue"

    /**
     * Feedback type *road closed*: closed road or one that does not allow vehicles
     */
    const val ROAD_CLOSED = "road_closed"

    /**
     * Feedback type *routing error*: poor instruction or route choice
     * (ambiguous or poorly-timed turn announcement, or a set of confusing turns)
     */
    const val ROUTING_ERROR = "routing_error"

    /**
     * Feedback type *route not allowed*
     */
    const val ROUTE_NOT_ALLOWED = "route_not_allowed"

    /**
     * Feedback type *looks incorrect*: wrong visual guidance
     */
    const val INCORRECT_VISUAL_GUIDANCE = "incorrect_visual_guidance"

    /**
     * Feedback type *incorrect audio guidance*: wrong audio guidance
     */
    const val INCORRECT_AUDIO_GUIDANCE = "incorrect_audio_guidance"

    /**
     * Feedback type *positioning issue*: wrong positioning
     */
    const val POSITIONING_ISSUE = "positioning_issue"

    /**
     * Feedback type *arrival information*: user's feelings is good about the arrival experience
     * as the device comes to the final destination
     */
    const val ARRIVAL_FEEDBACK_GOOD = "arrival_feedback_good"

    /**
     * Feedback type *arrival information*: user's feelings is not good about the arrival experience
     * as the device comes to the final destination
     */
    const val ARRIVAL_FEEDBACK_NOT_GOOD = "arrival_feedback_not_good"

    /**
     * Feedback source *reroute*: the user tapped a feedback button in response to a reroute
     */
    const val REROUTE = "reroute"

    /**
     * Feedback source *user*: the user tapped a feedback button
     */
    const val UI = "user"

    /**
     * Feedback description for *looks incorrect*: turn icon incorrect
     */
    const val TURN_ICON_INCORRECT = "turn_icon_incorrect"

    /**
     * Feedback description for *looks incorrect*: street name incorrect
     */
    const val STREET_NAME_INCORRECT = "street_name_incorrect"

    /**
     * Feedback description for *looks incorrect*: instruction unnecessary
     */
    const val INSTRUCTION_UNNECESSARY = "instruction_unnecessary"

    /**
     * Feedback description for *looks incorrect*: instruction missing
     */
    const val INSTRUCTION_MISSING = "instruction_missing"

    /**
     * Feedback description for *looks incorrect*: maneuver incorrect
     */
    const val MANEUVER_INCORRECT = "maneuver_incorrect"

    /**
     * Feedback description for *looks incorrect*: exit info incorrect
     */
    const val EXIT_INFO_INCORRECT = "exit_info_incorrect"

    /**
     * Feedback description for *looks incorrect*: lane guidance incorrect
     */
    const val LANE_GUIDANCE_INCORRECT = "lane_guidance_incorrect"

    /**
     * Feedback description for *looks incorrect*: incorrect speed limit
     */
    const val INCORRECT_SPEED_LIMIT = "incorrect_speed_limit"

    /**
     * Feedback description for *incorrect audio guidance*: guidance too early
     */
    const val GUIDANCE_TOO_EARLY = "guidance_too_early"

    /**
     * Feedback description for *incorrect audio guidance*: guidance too late
     */
    const val GUIDANCE_TOO_LATE = "guidance_too_late"

    /**
     * Feedback description for *incorrect audio guidance*: pronunciation incorrect
     */
    const val PRONUNCIATION_INCORRECT = "pronunciation_incorrect"

    /**
     * Feedback description for *incorrect audio guidance*: road name repeated
     */
    const val ROAD_NAME_REPEATED = "road_name_repeated"

    /**
     * Feedback description for *routing error*: route not drive-able
     */
    const val ROUTE_NOT_DRIVE_ABLE = "route_not_driveable"

    /**
     * Feedback description for *routing error*: route not preferred
     */
    const val ROUTE_NOT_PREFERRED = "route_not_preferred"

    /**
     * Feedback description for *routing error*: alternative route not expected
     */
    const val ALTERNATIVE_ROUTE_NOT_EXPECTED =
        "alternative_route_not_expected"

    /**
     * Feedback description for *routing error*: route included missing roads
     */
    const val ROUTE_INCLUDED_MISSING_ROADS = "route_included_missing_roads"

    /**
     * Feedback description for *routing error*: route had roads too narrow to pass
     */
    const val ROUTE_HAD_ROADS_TOO_NARROW_TO_PASS =
        "route_had_roads_too_narrow_to_pass"

    /**
     * Feedback description for *routing error*: routed down a one-way
     */
    const val ROUTED_DOWN_A_ONE_WAY = "routed_down_a_one_way"

    /**
     * Feedback description for *routing error*: turn was not allowed
     */
    const val TURN_WAS_NOT_ALLOWED = "turn_was_not_allowed"

    /**
     * Feedback description for *routing error*: cars not allowed on street
     */
    const val CARS_NOT_ALLOWED_ON_STREET = "cars_not_allowed_on_street"

    /**
     * Feedback description for *routing error*: street permanently blocked off
     */
    const val STREET_PERMANENTLY_BLOCKED_OFF =
        "street_permanently_blocked_off"

    /**
     * Feedback description for *road issue*: street temporarily blocked off
     */
    const val STREET_TEMPORARY_BLOCKED_OFF = "street_temporary_blocked_off"

    /**
     * Feedback description for *road issue*: missing road
     */
    const val MISSING_ROAD = "missing_road"

    /**
     * Feedback description for *wrong traffic*: congestion
     */
    const val TRAFFIC_CONGESTION = "traffic_congestion"

    /**
     * Feedback description for *wrong traffic*: moderate
     */
    const val TRAFFIC_MODERATE = "traffic_moderate"

    /**
     * Feedback description for *wrong traffic*: no traffic
     */
    const val TRAFFIC_NO = "traffic_no"

    /**
     * Feedback description for *arrival feedback not good*: wrong location
     */
    const val ARRIVAL_FEEDBACK_WRONG_LOCATION = "arrival_feedback_wrong_location"

    /**
     * Feedback description for *arrival feedback not good*: wrong entrance
     */
    const val ARRIVAL_FEEDBACK_WRONG_ENTRANCE = "arrival_feedback_wrong_entrance"

    /**
     * Feedback description for *arrival feedback not good*: confusing instructions
     */
    const val ARRIVAL_FEEDBACK_CONFUSING_INSTRUCTIONS = "arrival_feedback_confusing_instructions"

    /**
     * Feedback description for *arrival feedback not good*: this place is closed
     */
    const val ARRIVAL_FEEDBACK_THIS_PLACE_IS_CLOSED = "arrival_feedback_this_place_is_closed"

    /**
     * Feedback driver mode `trip`
     */
    internal const val DRIVER_MODE_TRIP = "trip"

    /**
     * Feedback driver mode `free drive`
     */
    internal const val DRIVER_MODE_FREE_DRIVE = "freeDrive"

    /**
     * Type of feedback mean WHAT happen
     */
    @Retention(AnnotationRetention.BINARY)
    @StringDef(
        INCORRECT_VISUAL,
        ROAD_ISSUE,
        TRAFFIC_ISSUE,
        OTHER_ISSUE,
        ROAD_CLOSED,
        ROUTING_ERROR,
        ROUTE_NOT_ALLOWED,
        INCORRECT_VISUAL_GUIDANCE,
        INCORRECT_AUDIO_GUIDANCE,
        POSITIONING_ISSUE,
        ARRIVAL_FEEDBACK_GOOD,
        ARRIVAL_FEEDBACK_NOT_GOOD
    )
    annotation class Type

    /**
     * Feedback source mean WHERE happen
     */
    @Retention(AnnotationRetention.BINARY)
    @StringDef(
        REROUTE,
        UI
    )
    annotation class Source

    /**
     * Detail description for different feedback type
     */
    @Retention(AnnotationRetention.BINARY)
    @StringDef(
        TURN_ICON_INCORRECT,
        STREET_NAME_INCORRECT,
        INSTRUCTION_UNNECESSARY,
        INSTRUCTION_MISSING,
        MANEUVER_INCORRECT,
        EXIT_INFO_INCORRECT,
        LANE_GUIDANCE_INCORRECT,
        INCORRECT_SPEED_LIMIT,
        GUIDANCE_TOO_EARLY,
        GUIDANCE_TOO_LATE,
        PRONUNCIATION_INCORRECT,
        ROAD_NAME_REPEATED,
        ROUTE_NOT_DRIVE_ABLE,
        ROUTE_NOT_PREFERRED,
        ALTERNATIVE_ROUTE_NOT_EXPECTED,
        ROUTE_INCLUDED_MISSING_ROADS,
        ROUTE_HAD_ROADS_TOO_NARROW_TO_PASS,
        ROUTED_DOWN_A_ONE_WAY,
        TURN_WAS_NOT_ALLOWED,
        CARS_NOT_ALLOWED_ON_STREET,
        STREET_PERMANENTLY_BLOCKED_OFF,
        STREET_TEMPORARY_BLOCKED_OFF,
        MISSING_ROAD,
        TRAFFIC_CONGESTION,
        TRAFFIC_MODERATE,
        TRAFFIC_NO,
        ARRIVAL_FEEDBACK_WRONG_LOCATION,
        ARRIVAL_FEEDBACK_WRONG_ENTRANCE,
        ARRIVAL_FEEDBACK_CONFUSING_INSTRUCTIONS,
        ARRIVAL_FEEDBACK_THIS_PLACE_IS_CLOSED
    )
    annotation class Description

    /**
     * Driver mode
     */
    @Retention(AnnotationRetention.BINARY)
    @StringDef(
        DRIVER_MODE_TRIP,
        DRIVER_MODE_FREE_DRIVE,
    )
    internal annotation class DriverMode
}
