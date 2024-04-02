import {SwiperOptions} from 'swiper/types/swiper-options';

export interface SwiperBreakpoint {
    [width: number]: SwiperOptions;

    [ratio: string]: SwiperOptions;
}
