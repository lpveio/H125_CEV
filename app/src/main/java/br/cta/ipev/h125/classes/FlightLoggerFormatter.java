
package br.cta.ipev.h125.classes;


import java.util.Locale;


import br.cta.ipev.h125.setup.Index;
import br.cta.isad.LogFormatter;

public class FlightLoggerFormatter implements LogFormatter {

    @Override
    public String header() {
        return "time, top, mem, xpa, xpc,  lat, lon, alt_msl, gs_kn, hdg, fli, auw, ff, nr,n1, fqty, tot, oil, n2, zpb, zpbmax, zpbmin, alt_rate , vb, vbmin, vbmax, hdg_014, imp_press, p, q, r,vert_vel, nx, ny, nz, theta,phi, sat,stt_press,total_press, tas, hdg_mag, hgd_true, ralt";
    }

    @Override
    public String format(double[] cvt) {

        double time = cvt[Index.TEMPO.ordinal()];
        double top = cvt[Index.TOP.ordinal()];
        double mem = cvt[Index.MEM.ordinal()];
        double xpa  = cvt[Index.XPA.ordinal()];
        double xpc  = cvt[Index.XPC.ordinal()];
        double lat  = cvt[Index.LAT.ordinal()];
        double lon   = cvt[Index.LONG.ordinal()];
        double alt_msl  = cvt[Index.ALT_MSL.ordinal()];
        double gs_kn  = cvt[Index.GS_KN.ordinal()];
        double hdg   = cvt[Index.HDG.ordinal()];
        double fli   = cvt[Index.FLI.ordinal()];
        double auw = cvt[Index.AUW.ordinal()];
        double ff = cvt[Index.FF.ordinal()];
        double nr = cvt[Index.NR.ordinal()];
        double n1 = cvt[Index.N1.ordinal()];
        double fqty = cvt[Index.FQTY.ordinal()];
        double fqtyp = cvt[Index.FQTYP.ordinal()];
        double tot = cvt[Index.TOP.ordinal()];
        double oil = cvt[Index.OIL_PRESS.ordinal()];
        double tq = cvt[Index.TQ.ordinal()];
        double n2 = cvt[Index.N2.ordinal()];
        double zpb = cvt[Index.ZPB.ordinal()];
        double zpbmin = cvt[Index.ZPBMax.ordinal()];
        double zpbmax = cvt[Index.ZPBMin.ordinal()];
        double alt_rate = cvt[Index.ALT_RATE.ordinal()];
        double vb = cvt[Index.VB.ordinal()];
        double vbmin = cvt[Index.VBMin.ordinal()];
        double vbmax = cvt[Index.VBMax.ordinal()];
        double hdg_014 = cvt[Index.HDG_014.ordinal()];
        double imp_press = cvt[Index.IMP_PRESS.ordinal()];
        double p = cvt[Index.P.ordinal()];
        double q = cvt[Index.Q.ordinal()];
        double r = cvt[Index.R.ordinal()];
        double vert_vel = cvt[Index.VERT_VEL.ordinal()];
        double nx = cvt[Index.NX.ordinal()];
        double ny = cvt[Index.NY.ordinal()];
        double nz = cvt[Index.NZ.ordinal()];
        double theta = cvt[Index.THETA.ordinal()];
        double phi = cvt[Index.PHI.ordinal()];
        double sat = cvt[Index.SAT.ordinal()];
        double stt_press = cvt[Index.STT_PRESS.ordinal()];
        double total_press = cvt[Index.TOTAL_PRESS.ordinal()];
        double tas = cvt[Index.TAS.ordinal()];
        double hdg_mag = cvt[Index.HDG_MAG.ordinal()];
        double hgd_true = cvt[Index.HDG_TRUE.ordinal()];
        double ralt = cvt[Index.RALT.ordinal()];
        
        return String.format(Locale.US,
                "%.3f,%.0f,%.0f,%.1f,%.1f,%.6f,%.6f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f, %.2f, %.2f", time, top, mem, xpa, xpc,  lat, lon, alt_msl, gs_kn, hdg, fli, auw, ff, nr,n1, fqty, fqtyp,  tot, oil,tq, n2, zpb, zpbmax, zpbmin, alt_rate,
                vb, vbmin, vbmax, hdg_014, imp_press, p, q, r,vert_vel, nx, ny, nz, theta,phi, sat,stt_press,total_press, tas, hdg_mag, hgd_true, ralt);
        }


}