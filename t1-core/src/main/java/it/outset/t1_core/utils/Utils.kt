package it.outset.t1_core.utils

class Utils {
    companion object {
        /**
         * Calcola il checksum CRC per una stringa NMEA.
         * Il CRC è calcolato tramite XOR di ogni byte della stringa,
         * escludendo il '$' iniziale e tutto ciò che segue '*'.
         *
         * @param nmeaString La stringa NMEA da cui calcolare il CRC
         * @return Il checksum come stringa esadecimale a 2 cifre
         */
        fun calculateNmeaCrc(nmeaString: String): String {
            var checksum = 0

            // Trova l'indice di inizio (dopo '$') e fine (prima di '*')
            val startIndex = if (nmeaString.startsWith('$')) 1 else 0
            val endIndex = nmeaString.indexOf('*').let {
                if (it != -1) it else nmeaString.length
            }

            // Calcola XOR di ogni byte
            for (i in startIndex until endIndex) {
                checksum = checksum xor nmeaString[i].code
            }

            // Restituisci come stringa esadecimale a 2 cifre (uppercase)
            return checksum.toString(16).uppercase().padStart(2, '0')
        }
    }
}
