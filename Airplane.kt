import java.lang.Math.abs
import java.lang.Math.pow
import kotlin.random.Random

class Airplane(val registration: String) : Observable {

    private var airport: Airport? = null
    private var atis: ATIS? = null


    private fun landingUpdate(status: LandingStatus) = notifyObservers(LandingUpdate(status))

    private fun landingFailure() {
        println("$registration: Landing unsuccessful, attempting a goaround")
        landingUpdate(LandingStatus.GoAround)
    }

    private fun landingSuccess() {
        println("$registration: Landing on rwy ${atis?.activeRunway} successful, taxiing to gate")
        landingUpdate(LandingStatus.Success)
    }


    private val relativeWindDir
        get() = abs((atis?.runwayHeading!! - atis?.windDirection!!) / 10)

    // Calculates the chance to land the aircraft successfully in case
    // The aircraft is under crosswind
    // Chance to land decreases exponentially with wind speed
    // Chance to land is calculated using the following formula and is expressed as a percentage
    // (100^2 - (speed[kts] * 4)^2) / 100
    // Meaning aircraft cannot land under a crosswind of 25 knots
    private fun chanceToLand(windSpeed: Int)
        = (10000 - pow(windSpeed * 4.0, 2.0).toInt()) / 100


    private fun attemptLanding() {

        if (atis == null) {
            return landingFailure()
        }

        // Land successfully if no wind or headwind
        if (relativeWindDir >= 13 || atis?.windSpeed!! < 10) {
            return landingSuccess()
        }

        // Go around if tail wind
        if (relativeWindDir <= 5) {
            return landingFailure()
        }

        // If crosswind, calculate chance to land successfully based on wind speed
        val chance = chanceToLand(atis?.windSpeed!!)
        val rand = Random.nextInt(0, 100)

        return if (rand >= chance) landingSuccess() else landingFailure()

    }

    override fun notify(m: Message) {
        if (m is ATIS) {
            atis = m
        }
        else if (m is LandingClearance) {
            attemptLanding()
        }
    }

    override fun notifyObservers(m: Message) {
        airport?.notify(this, m)
    }

    override fun addObserver(o: Observer) {
        airport = o as Airport
    }

    override fun removeObserver(o: Observer) {
        if (airport === o) {
            airport = null
        }
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Airplane

        return registration == other.registration

    }

    override fun hashCode(): Int {
        return registration.hashCode()
    }

}
