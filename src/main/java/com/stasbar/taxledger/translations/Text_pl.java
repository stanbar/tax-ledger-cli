package com.stasbar.taxledger.translations;

import java.util.ListResourceBundle;

import static com.stasbar.taxledger.translations.Text.*;
import static com.stasbar.taxledger.translations.Text.Actions.*;
import static com.stasbar.taxledger.translations.Text.Exceptions.*;
import static com.stasbar.taxledger.translations.Text.Logger.ERROR;
import static com.stasbar.taxledger.translations.Text.Logger.INFO;
import static com.stasbar.taxledger.translations.Text.OperationType.BUY;
import static com.stasbar.taxledger.translations.Text.OperationType.SELL;
import static com.stasbar.taxledger.translations.Text.Summary.*;

public class Text_pl extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ACTION, "Wpisz akcje do wykonania"},
                {WRONG_ACTION, "Nieobsługiwana operacja"},
                {ENTER_EXCHANGE_NAME, "Wprowadź nazwę giełdy"},
                {ADDED_CREDENTIALS, "Pomyślnie dodano kody uwierzytelniające dla "},
                {GROSS_INCOME, "Przychód (Brutto)"},
                {EXPENSE, "Koszty"},
                {NET_INCOME, "Dochód (Netto)"},
                {SUMMARY, "Summary"},
                {UNKNOWN_LANGUAGE, "Nieznany język"},
                {CREDENTIALS_SAVED, "Klucze autoryzujące zostały zapisane do pliku %s"},
                {DONATE, "Program został stworzony dla społeczności kryptowalut za darmo,\n" +
                        " jeśli jednak okazał się dla Ciebie przydatny i chcesz aby został dalej rozwijany, możesz przekazać darowiznę na jeden z poniższych adresów. \n" +
                        " Dziękuje "+ new String(Character.toChars(0x1F60A))},

                {TRANSACTIONS_TITLE, "Transakcje"},
                {TRANSACTIONS_DESC, "Wyświetl i zapisz do pliku historie transakcji"},
                {CONTACT_TITLE, "Kontakt"},
                {CONTACT_DESC, "Wyświetla informacje kontaktowe"},
                {EXCHANGES_TITLE, "Giełdy"},
                {EXCHANGES_DESC, "Konfiguruje kody dostępu do API giełd"},
                {DONATE_TITLE, "Darowizny"},
                {DONATE_DESC, "Wyświetla liste adresów do darowizn"},
                {EXIT_TITLE, "Zamknij"},
                {EXIT_DESC, ""},
                {INFO, "(INFO) "},
                {ERROR, "(BŁĄD) "},
                {BUY, "Buy"},
                {SELL, "Sell"},
                {TOO_MANY_ARGS, "Za dużo kodów dla "},
                {CREDENTIALS, "Podałeś niepoprawny %s dla %s, klucz powinien mieć %d znaków"},
                {API_NOT_SET, "Nie wystarczająca ilość kodów uwierzytelniających aby połączyć się z API %s"},
                {EXCHANGE, "Giełda"},
                {TYPE, "Typ"},
                {DATE, "Data"},
                {RATE, "Kurs"},
                {GET, "Kupiłem"},
                {PAID, "Zapłaciłem"},
                {INVALID_ARG, "Niepoprawny argument \"%s\""},
                {TRANSACTIONS_SAVED, "Transakcje zostały zapisane do pliku %s"},
                {LOAD_COMPLETE, "Pomyślnie wczytano %d transakcji"},
        };
    }


}
