/**
 * A standard set of {@link DataSize} units.
 *
 * <p>The unit prefixes used in this class are
 * <a href="https://en.wikipedia.org/wiki/Binary_prefix">binary prefixes</a>
 * indicating multiplication by powers of 2. The following table displays the
 * enum constants defined in this class and corresponding values.
 *
 * <p>
 * <table border="1">
 * <tr><th>Constant</th><th>Data Size</th><th>Power&nbsp;of&nbsp;2</th><th>Size in Bytes</th></tr>
 * <tr><td>{@link #BYTES}</td><td>1B</td><td>2^0</td><td>1</td></tr>
 * <tr><td>{@link #KILOBYTES}</td><td>1KB</td><td>2^10</td><td>1,024</td></tr>
 * <tr><td>{@link #MEGABYTES}</td><td>1MB</td><td>2^20</td><td>1,048,576</td></tr>
 * <tr><td>{@link #GIGABYTES}</td><td>1GB</td><td>2^30</td><td>1,073,741,824</td></tr>
 * <tr><td>{@link #TERABYTES}</td><td>1TB</td><td>2^40</td><td>1,099,511,627,776</td></tr>
 * </table>
 *
 * @see DataSize
 */
export type DataUnit = Bytes | Megabytes;

class DataUnitClass {
    constructor(
        readonly suffix: string,
        readonly bytes: number,
    ) {
    }
}

class Bytes extends DataUnitClass {
    constructor() {
        super('B', 1);
    }
}

/**
 * Bytes per Kilobyte.
 */
const BYTES_PER_KB = 1024;

/**
 * Bytes per Megabyte.
 */
const BYTES_PER_MB = BYTES_PER_KB * 1024;

class Megabytes extends DataUnitClass {
    constructor() {
        super('MB', BYTES_PER_MB);
    }
}

export const BYTES = new Bytes();
export const MEGABYTES = new Megabytes();
const ALL_UNITS: DataUnit[] = [BYTES, MEGABYTES];

export function fromSuffix(suffix: string): DataUnit {
    const dataUnit: DataUnit = ALL_UNITS.find(candidate => candidate.suffix === suffix);
    if (dataUnit) {
        return dataUnit;
    } else {
        throw new Error('Unknown data unit suffix \'' + suffix + '\'');
    }
}
