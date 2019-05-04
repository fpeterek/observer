
interface Observer {

    fun notify(sender: Observable, m: Message)
    fun notifyObservables(m: Message)
    fun addObservable(o: Observable)
    fun removeObservable(o: Observable)

}
