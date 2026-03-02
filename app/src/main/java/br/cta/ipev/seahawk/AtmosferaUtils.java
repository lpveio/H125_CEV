package br.cta.ipev.seahawk;

public class AtmosferaUtils {

    private static final double RO = 287.053;
    public double TempMeta;

    public double calcularPa(double altitudeFt) {
        return 1013.25 * Math.pow(1 - 6.87559e-6 * altitudeFt, 5.25588);
    }

    public double calcularK(double tempCelsius) {
        return tempCelsius + 273.15;
    }

    public double calcularAltitudeZP(double pa) {
        return (1 - Math.pow(pa / 1013.25, 1 / 5.25588)) / 6.87559e-6;
    }

    public double calcularRho(double pa, double tempKelvin) {
        return pa * 100 / (RO * tempKelvin);
    }

    public double calcularSigma(double rho) {
        return rho / 1.225;
    }

    public double calcularRho2(double massa, double msigma) {
        return (massa / msigma) * 1.225;
    }

    public double calcularPaux(double tar, double painic, double rho, double zpinic) {
        double taux = tar;
        double roinic = (painic * 100) / (RO * tar);
        double paux = (painic * rho * tar) / (roinic * tar);
        double zpaux = calcularAltitudeZP(paux);
        double dzp = zpaux - zpinic;

        while (Math.abs(dzp) >= 10.0) {
            double dtemp = (2.0 * dzp) / 1000.0;
            taux -= dtemp;
            TempMeta = taux;
            double zpaux2 = zpaux;
            double paux2 = (painic * rho * taux) / (roinic * tar);
            zpaux = calcularAltitudeZP(paux2);
            dzp = zpaux - zpaux2;
        }

        double zpi = Math.round(zpaux / 20) * 20;

        return zpi; // Retorna altitude ajustada
    }
}
