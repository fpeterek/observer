

interface Observable {

    fun notify(m: Message)
    fun notifyObservers(m: Message)
    fun addObserver(o: Observer)
    fun removeObserver(o: Observer)

}
