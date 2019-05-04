
class ATIS (
    val windDirection: Int,
    val windSpeed: Int,
    val activeRunway: String
) : Message() {

    val runwayHeading: Int
        get() = activeRunway.filter { it.isDigit() }.toInt() * 10

}

enum class LandingStatus {
    Success,
    GoAround
}

class LandingUpdate(
    val status: LandingStatus
) : Message() {

    val success
        get() = status == LandingStatus.Success

}

class LandingClearance : Message()
