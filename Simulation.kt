import kotlin.random.Random

class Simulation {

    private val airport: Airport = Airport(Random.nextInt(0, 18), "OMDB", "DXB")

    private fun randRegistration(): String {

        val begin = listOf("D", "A7", "OK", "N").random()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var end = ""

        for (i in 1..(5-begin.length)) {
            end += chars.random()
        }

        return "$begin-$end"

    }

    private fun addPlane() {
        val plane = Airplane(randRegistration())
        airport.addObservable(plane)
        plane.addObserver(airport)
    }

    tailrec fun run() {

        if (Random.nextBoolean()) {
            addPlane()
        }

        airport.update()
        Thread.sleep(3000)
        run()

    }

}
