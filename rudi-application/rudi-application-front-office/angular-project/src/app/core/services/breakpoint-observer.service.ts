import {BreakpointObserver, BreakpointState} from '@angular/cdk/layout';
import {Injectable} from '@angular/core';

/**
 * Définit l'oobjet qui donne des infos sur le contexte de visualisation de l'appli RUDI
 */
export interface MediaSize {
    /**
     * Est-ce très petit
     */
    isXs: boolean;

    /**
     * Est-ce petit
     */
    isSm: boolean;

    /**
     * Est-ce moyen
     */
    isMd: boolean;

    /**
     * Est-ce grand
     */
    isLg: boolean;

    /**
     * Est-ce très grand
     */
    isXl: boolean;

    /**
     * Est-ce vraiment très grand
     */
    isXxl: boolean;

    /**
     * Est on sur un device desktop (PC)
     */
    isDeviceDesktop: boolean;

    /**
     * Est on sur un device mobile
     */
    isDeviceMobile: boolean;
}

export type NgClassObject = { [p: string]: boolean };

class ScreenBreakpoints {
    public static XSBREAKPOINT = '(min-width:0px) and (max-width:459px)';
    public static SMBREAKPOINT = '(min-width:460px) and (max-width:767px)';
    public static MDBREAKPOINT = '(min-width:768px) and (max-width:1023px)';
    public static LGBREAKPOINT = '(min-width:1024px) and (max-width:1439px)';
    public static XLBREAKPOINT = '(min-width:1440px) and (max-width:1824px)';
    public static XXLBREAKPOINT = '(min-width:1825px)';
}

@Injectable({
    providedIn: 'root'
})
export class BreakpointObserverService {

    /**
     * Instanciation d'un singleton mediaSize
     */
    mediaSize: MediaSize = {
        isXs: false,
        isSm: false,
        isMd: false,
        isLg: false,
        isXl: false,
        isXxl: false,
        get isDeviceDesktop(): boolean {
            // Pour savoir si on est sur desktop, on regarde l'état du mediaSize
            return this.isLg || this.isXl || this.isXxl;
        },
        get isDeviceMobile(): boolean {
            // Pour savoir si on est sur mobile, on regarde l'état du mediaSize
            return this.isXs || this.isSm || this.isMd;
        }
    };

    /**
     * Constructeur
     * @param breakpointObserver service observation du contexte de visualisation
     */
    constructor(private breakpointObserver: BreakpointObserver) {
        this.getMediaSize();
    }

    /**
     * Méthode appelée pour récupérer le mediaSize dans un component
     */
    getMediaSize(): MediaSize {
        // Mode Small device
        this.breakpointObserver.observe(ScreenBreakpoints.XSBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isXs = state.matches;
        });

        // Mode Small device
        this.breakpointObserver.observe(ScreenBreakpoints.SMBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isSm = state.matches;
        });

        // Mode Medium device
        this.breakpointObserver.observe(ScreenBreakpoints.MDBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isMd = state.matches;
        });

        // Mode Large medium
        this.breakpointObserver.observe(ScreenBreakpoints.LGBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isLg = state.matches;
        });
        // Mode X Large medium
        this.breakpointObserver.observe(ScreenBreakpoints.XLBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isXl = state.matches;
        });
        // Mode XX Large
        this.breakpointObserver.observe(ScreenBreakpoints.XXLBREAKPOINT).subscribe((state: BreakpointState) => {
            this.mediaSize.isXxl = state.matches;
        });

        return this.mediaSize;
    }

    /**
     * Construction d'un ngClass à partir du mediaSize
     * @param baseClass classe de base
     */
    getNgClassFromMediaSize(baseClass: string): NgClassObject {
        return {
            [`${baseClass}-xs`]: this.mediaSize.isXs,
            [`${baseClass}-sm`]: this.mediaSize.isSm,
            [`${baseClass}-md`]: this.mediaSize.isMd,
            [`${baseClass}-lg`]: this.mediaSize.isLg,
            [`${baseClass}-xl`]: this.mediaSize.isXl,
            [`${baseClass}-xxl`]: this.mediaSize.isXxl
        };
    }
}
