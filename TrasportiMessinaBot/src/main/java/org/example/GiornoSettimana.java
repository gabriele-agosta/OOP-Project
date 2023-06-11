package org.example;
 enum GiornoSettimana {
    Lunedi,
    Martedi,
    Mercoledi,
    Giovedi,
    Venerdi,
    Sabato,
    Domenica;

    public static GiornoSettimana fromString(String value) {
        if (value != null) {
            switch (value.toLowerCase()) {
                case "lunedi":
                    return Lunedi;
                case "martedi":
                    return Martedi;
                case "mercoledi":
                    return Mercoledi;
                case "giovedi":
                    return Giovedi;
                case "venerdi":
                    return Venerdi;
                case "sabato":
                    return Sabato;
                case "domenica":
                    return Domenica;
            }
        }
        throw new IllegalArgumentException("Invalid day of week value: " + value);
    }
}
