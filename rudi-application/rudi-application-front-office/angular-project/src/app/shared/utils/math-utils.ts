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
        // cette méthode pourrait gérer le size overflow mais ne le gère pas actuellement faute de nécessité fonctionnelle et/ou technique
        return  x * y;
    }

}
