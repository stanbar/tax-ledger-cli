/*
 * Copyright (c) 2017 Stanislaw stasbar Baranski
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

import org.jetbrains.annotations.NotNull;

import java.util.ListResourceBundle;

import static com.stasbar.taxledger.translations.Text.Actions.*;
import static com.stasbar.taxledger.translations.Text.Exceptions.*;
import static com.stasbar.taxledger.translations.Text.Logger.ERROR;
import static com.stasbar.taxledger.translations.Text.Logger.INFO;
import static com.stasbar.taxledger.translations.Text.OperationType.BUY;
import static com.stasbar.taxledger.translations.Text.OperationType.SELL;
import static com.stasbar.taxledger.translations.Text.Summary.*;

public class Text extends ListResourceBundle {


    public static final String UNKNOWN_LANGUAGE = "unknown_language";
    public static final String CREDENTIALS_SAVED = "credentials_saved";
    public static final String DONATE = "donate";
    public static final String ACTION = "action";
    public static final String WRONG_ACTION = "wrong_action";
    public static final String ENTER_EXCHANGE_NAME = "enter_exchange_name";
    public static final String ADDED_CREDENTIALS = "added_credentials";

    public static final String EXCHANGE = "exchange";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String RATE = "rate";
    public static final String GET = "get";
    public static final String PAID = "paid";
    public static final String TRANSACTIONS_SAVED = "transactions_saved";
    @NotNull
    public static final String LOAD_COMPLETE = "load_complete";


    public static class Summary {
        public static final String GROSS_INCOME = "gross_income";
        public static final String EXPENSE = "expense";
        public static final String NET_INCOME = "net_income";
        public static final String SUMMARY = "summary";
    }

    public static class Actions {
        public static final String TRANSACTIONS_TITLE = "transactions_title";
        public static final String TRANSACTIONS_DESC = "transactions_desc";
        public static final String CONTACT_TITLE = "contact_title";
        public static final String CONTACT_DESC = "contact_desc";
        public static final String EXCHANGES_TITLE = "exchanges_title";
        public static final String EXCHANGES_DESC = "exchanges_desc";
        public static final String DONATE_TITLE = "donate_title";
        public static final String DONATE_DESC = "donate_desc";
        public static final String EXIT_TITLE = "exit_title";
        public static final String EXIT_DESC = "exit_desc";
    }

    public static class Logger {
        public static final String INFO = "info";
        public static final String ERROR = "error";
    }


    public static class OperationType {
        public static final String BUY = "buy";
        public static final String SELL = "sell";
    }

    public static class Exceptions {
        public static final String TOO_MANY_ARGS = "too_many_args";
        public static final String CREDENTIALS = "credentials_exc";
        public static final String API_NOT_SET = "api_not_set";
        @NotNull
        public static final String INVALID_ARG = "invalid_arg";
    }

    @Override
    protected Object[][] getContents() {
        return content;
    }

    private static final Object[][] content = new Object[][]{
            {ACTION, "Enter actions to perform"},
            {WRONG_ACTION, "Wrong action"},
            {ENTER_EXCHANGE_NAME, "Please enter the exchange name"},
            {ADDED_CREDENTIALS, "Successfully added credentials for "},
            {GROSS_INCOME, "Gross Income"},
            {EXPENSE, "Expense"},
            {NET_INCOME, "Net Income"},
            {SUMMARY, "Summary"},
            {UNKNOWN_LANGUAGE, "Unknown language"},
            {CREDENTIALS_SAVED, "Credentials have been saved to %s"},
            {DONATE, "The program is created for free to support the crypto currency community,\n" +
                    " but if you found it helpful and want keep the development in progress you can donate one of the addresses. \n" +
                    " Thank You " + new String(Character.toChars(0x1F60A))},
            {TRANSACTIONS_TITLE, "Transactions"},
            {TRANSACTIONS_DESC, "Transactions history"},
            {CONTACT_TITLE, "Contact"},
            {CONTACT_DESC, "Prints the contact information"},
            {EXCHANGES_TITLE, "Credentials"},
            {EXCHANGES_DESC, "Configure exchange API credentials"},
            {DONATE_TITLE, "Donate"},
            {DONATE_DESC, "Prints the donation addresses"},
            {EXIT_TITLE, "Exit"},
            {EXIT_DESC, "Exit the program"},
            {INFO, "(INFO) "},
            {ERROR, "(ERROR) "},
            {BUY, "Buy"},
            {SELL, "Sell"},
            {TOO_MANY_ARGS, "Too many credential for "},
            {CREDENTIALS, "You provided invalid %s for %s, it should be %d length long"},
            {API_NOT_SET, "Not enough credentials to connect to %s API"},
            {EXCHANGE, "Exchange"},
            {TYPE, "Type"},
            {DATE, "Date"},
            {RATE, "Rate"},
            {GET, "Get"},
            {PAID, "Paid"},
            {INVALID_ARG, "Invalid argument \"%s\" for transactions action"},
            {TRANSACTIONS_SAVED, "Transactions have been saved to %s"},
            {LOAD_COMPLETE, "Successfully loaded %d transactions"},


    };
}
