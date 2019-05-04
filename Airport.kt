import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class Airport(runway: Int, val icao: String, val iata: String) : Observer {

    private var windDirection: Int = 0
    private var windSpeed: Int = 0
    private val runways: Pair<Int, Int>
    private val planes = mutableListOf<Airplane>()

    init {
        val first = if (runway < 18) runway else runway - 18
        val second = first + 18
        runways = Pair(first, second)
    }

    private fun log(msg: String) {
        println("$iata/$icao: $msg")
    }

    private fun grantClearance(airplane: Airplane) {
        airplane.notify(LandingClearance())
    }

    private val activeRunway
        get() = if (Math.abs((runways.first * 10 - windDirection) / 10) > 18) {
            runways.first
        } else {
            runways.second
        }

    private fun formatRwy(rwy: Int) = if (rwy / 10 == 0)  "0$rwy" else "$rwy"


    private val atisMessage
        get() = ATIS(windDirection, windSpeed, formatRwy(activeRunway))

    private fun sendAtisMessage() {

        val msg = atisMessage

        for (plane in planes) {
            plane.notify(msg)
        }

    }

    private fun randomWindDirection() = Random.nextInt(0, 360)

    // Generate a random wind speed
    // Algorithm is strongly biased towards speeds around 15 knots
    private fun randomWindSpeed(): Int {

        val sign = if (Random.nextBits(1) == 1) 1 else -1
        val x = Random.nextDouble(0.1, 10.0) * sign

        val speed = (7.0 / x).roundToInt() + 15

        val boundSpeed = Math.min(45, max(0, speed))

        return boundSpeed
    }

    private fun updateWeatherInfo() {

        windDirection = randomWindDirection()
        windSpeed = randomWindSpeed()

    }

    fun update() {

        updateWeatherInfo()
        sendAtisMessage()

        if (planes.isEmpty()) {
            log("idle")
            return
        }

        grantClearance(planes.first())

    }

    override fun notify(sender: Observable, m: Message) {

        if (m !is LandingUpdate) {
            return
        }

        if (m.success) {
            removeObservable(sender)
        }

    }

    override fun notifyObservables(m: Message) {

        for (aircraft in planes) {
            aircraft.notify(m)
        }

    }

    override fun addObservable(o: Observable) {
        planes.add(o as Airplane)
        log("${o.registration} added to queue")
        log("Aircraft currently in queue - ${planes.size}")
    }

    override fun removeObservable(o: Observable) {

        o as Airplane

        for (i in planes.indices) {
            if (planes[i] == o) {
                planes.removeAt(i)
                break
            }
        }

    }
}
