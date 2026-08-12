package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UTIL — metodos utilitarios para conversao de datas.
 * Centraliza o formato dd/MM/yyyy em um unico lugar.
 */
public class DateUtil {

    private static final String FORMATO = "dd/MM/yyyy";

    private DateUtil() {} // classe utilitaria — nao instanciavel

    /** Converte String "dd/MM/yyyy" para Date. Retorna null se formato invalido. */
    public static Date parse(String texto) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATO);
        sdf.setLenient(false); // rejeita datas como 30/02/2024
        try {
            return sdf.parse(texto);
        } catch (ParseException e) {
            return null;
        }
    }

    /** Converte Date para String no formato dd/MM/yyyy. */
    public static String formatar(Date data) {
        if (data == null) return "";
        return new SimpleDateFormat(FORMATO).format(data);
    }
}
