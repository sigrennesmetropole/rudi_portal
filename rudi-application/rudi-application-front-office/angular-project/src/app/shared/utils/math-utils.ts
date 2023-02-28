export class MathUtils {

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows a {@code long}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @see java.lang.Math.multiplyExact(long, long)
     */
    static multiplyExact(x: number, y: number): number {
        const r = x * y;
        // TODO
        // const ax = Math.abs(x);
        // const ay = Math.abs(y);
        // if (((ax | ay) >>> 31 !== 0)) {
        //     // Some bits greater than 2^31 that might cause overflow
        //     // Check the result using the divide operator
        //     // and check for the special case of Long.MIN_VALUE * -1
        //     if (((y != 0) && (r / y != x)) ||
        //         (x == Long.MIN_VALUE && y == -1)) {
        //         throw new ArithmeticException('long overflow');
        //     }
        // }
        return r;
    }

}
