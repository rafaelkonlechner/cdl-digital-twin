package at.ac.tuwien.big

open class Observable<T> {

    private val subscribers = mutableListOf<(T) -> Unit>()

    fun subscribe(subscriber: (T) -> Unit) {
        subscribers.add(subscriber)
    }

    protected fun notify(msg: T) {
        for (s in subscribers) {
            s(msg)
        }
    }
}
