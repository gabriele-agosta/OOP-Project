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
                case "lunedi", "monday":
                    return Lunedi;
                case "martedi", "tuesday":
                    return Martedi;
                case "mercoledi", "wednesday":
                    return Mercoledi;
                case "giovedi", "thursday":
                    return Giovedi;
                case "venerdi", "friday":
                    return Venerdi;
                case "sabato", "saturday":
                    return Sabato;
                case "domenica", "sunday":
                    return Domenica;
            }
        }
        throw new IllegalArgumentException("Invalid day of week value: " + value);
    }
}
