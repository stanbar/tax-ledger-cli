/*
 * Copyright (c) 2018 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            taxledger@stasbar.com
 */

package com.stasbar.taxledger.translations;

import java.util.ListResourceBundle;

import static com.stasbar.taxledger.translations.Text.*;
import static com.stasbar.taxledger.translations.Text.Actions.*;
import static com.stasbar.taxledger.translations.Text.Exceptions.*;
import static com.stasbar.taxledger.translations.Text.Logger.ERROR;
import static com.stasbar.taxledger.translations.Text.Logger.INFO;
import static com.stasbar.taxledger.translations.Text.Summary.*;

public class Text_pl extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ACTION, "Wpisz akcje do wykonania"},
                {WRONG_ACTION, "Nieobsługiwana operacja"},
                {ENTER_EXCHANGE_NAME, "Wprowadź nazwę giełdy"},
                {ADDED_CREDENTIALS, "Pomyślnie wczytano kody uwierzytelniające dla "},
                {GROSS_INCOME, "Przychód (Brutto)"},
                {EXPENSE, "Koszty"},
                {EXPENSE_WITH_FEE, "Koszty (w tym prowizje)"},
                {NET_INCOME, "Dochód (Netto)"},
                {SUMMARY, "Podsumowanie"},
                {UNKNOWN_LANGUAGE, "Nieznany język"},
                {CREDENTIALS_SAVED, "Klucze autoryzujące zostały zapisane do pliku %s"},
                {DONATE, "Program został stworzony dla społeczności kryptowalut za darmo,\n" +
                        " jeśli jednak okazał się dla Ciebie przydatny i chcesz aby został dalej rozwijany, możesz przekazać darowiznę na jeden z poniższych adresów. \n" +
                        " Dziękuje "+ new String(Character.toChars(0x1F60A))},

                {TRANSACTIONS_TITLE, "Transakcje"},
                {TRANSACTIONS_DESC, "Wyświetl i zapisz do pliku historie transakcji"},
                {CONTACT_TITLE, "Kontakt"},
                {CONTACT_DESC, "Wyświetla informacje kontaktowe"},
                {EXCHANGES_TITLE, "Gieldy"},
                {EXCHANGES_DESC, "Konfiguruje kody dostępu do API giełd"},
                {DONATE_TITLE, "Darowizny"},
                {DONATE_DESC, "Wyświetla liste adresów do darowizn"},
                {EXIT_TITLE, "Zamknij"},
                {EXIT_DESC, ""},
                {INFO, "(INFO) "},
                {ERROR, "(BŁĄD) "},
                {BUY, "Buy"},
                {SELL, "Sell"},
                {FEE, "Prowizja"},
                {WITHDRAW, "Wypłata"},
                {DEPOSIT, "Wpłata"},
                {UNKNOWN, "Nieznane"},
                {AFFILIATE_INCOME, "Afiliacje"},
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
                {NO_OPERATIONS, "Nie znaleziono żandych operacji"},
                {TOTAL_OPERATIONS, "Łącznie %d operacji"},
        };
    }


}
