package net.prismclient.util

/**
 * Specifies a class which can be duplicated with the exact values.
 *
 * @author Winter
 */
interface Copyable<T : Copyable<T>> {
    /**
     * Returns an identical copy of the current instance.
     */
    fun copy(): T
}