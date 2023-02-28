import {ErrorWithCause} from './error-with-cause';
import {BYTES, DataUnit, fromSuffix} from './data-unit';
import {MathUtils} from '../utils/math-utils';
import {StringUtils} from '../utils/string-utils';

const PATTERN = /^([+\-]?\d+)([a-zA-Z]{0,2})$/;

/**
 * Bytes per Kilobyte.
 */
const BYTES_PER_KB = 1024;

/**
 * Bytes per Megabyte.
 */
const BYTES_PER_MB = BYTES_PER_KB * 1024;

/**
 * Bytes per Gigabyte.
 */
const BYTES_PER_GB = BYTES_PER_MB * 1024;

/**
 * Bytes per Terabyte.
 */
const BYTES_PER_TB = BYTES_PER_GB * 1024;

/**
 * Taille de données. Adaptation de la classe <code>org.springframework.util.unit.DataSize</code>.
 *
 * Exemple de propriétés Spring utilisant ce format :
 *
 * <ul>
 *     <li>spring.servlet.multipart.max-file-size</li>
 *     <li>spring.servlet.multipart.max-request-size</li>
 * </ul>
 *
 * Exemple : <code>10MB</code>.
 */
export class DataSize {
    private constructor(private bytes: number) {
    }


    /**
     * Obtain a {@link DataSize} representing the specified number of bytes.
     * @param bytes the number of bytes, positive or negative
     * @return a {@link DataSize}
     */
    static ofBytes(bytes: number): DataSize {
        return new DataSize(bytes);
    }

    /**
     * Obtain a {@link DataSize} representing the specified number of megabytes.
     * @param megabytes the number of megabytes, positive or negative
     * @return a {@link DataSize}
     */
    static ofMegabytes(megabytes: number): DataSize {
        return new DataSize(MathUtils.multiplyExact(megabytes, BYTES_PER_MB));
    }


    /**
     * Obtain a {@link DataSize} representing an amount in the specified {@link DataUnit}.
     * @param amount the amount of the size, measured in terms of the unit,
     * positive or negative
     * @return a corresponding {@link DataSize}
     */
    static of(amount: number, unit: DataUnit): DataSize {
        if (!unit) {
            throw new Error('Unit must not be null');
        }
        return new DataSize(MathUtils.multiplyExact(amount, unit.bytes));
    }


    /**
     * Obtain a {@link DataSize} from a text string such as {@code 12MB} using
     * the specified default {@link DataUnit} if no unit is specified.
     * <p>
     * The string starts with a number followed optionally by a unit matching one of the
     * supported {@linkplain DataUnit suffixes}.
     * <p>
     * Examples:
     * <pre>
     * "12KB" -- parses as "12 kilobytes"
     * "5MB"  -- parses as "5 megabytes"
     * "20"   -- parses as "20 kilobytes" (where the {@code defaultUnit} is {@link DataUnit#KILOBYTES})
     * </pre>
     * @param text the text to parse
     * @return the parsed {@link DataSize}
     */
    static parse(text: string, defaultUnit?: DataUnit): DataSize {
        if (!text) {
            throw new Error('Text must not be null');
        }
        try {
            const matcher: RegExpExecArray = PATTERN.exec(text);
            if (!matcher) {
                throw new Error('Does not match data size pattern');
            }

            const unit = DataSize.determineDataUnit(matcher[2], defaultUnit);
            const amount = +matcher[1];
            return DataSize.of(amount, unit);
        } catch (ex) {
            throw new ErrorWithCause('\'' + text + '\' is not a valid data size', ex);
        }
    }

    static determineDataUnit(suffix: string, defaultUnit: DataUnit = BYTES): DataUnit {
        return StringUtils.hasLength(suffix) ? fromSuffix(suffix) : defaultUnit;
    }

    /**
     * Return the number of bytes in this instance.
     * @return the number of bytes
     */
    toBytes(): number {
        return this.bytes;
    }

    /**
     * Return the number of megabytes in this instance.
     * @return the number of megabytes
     */
    toMegabytes(): number {
        return this.bytes / BYTES_PER_MB;
    }

}
