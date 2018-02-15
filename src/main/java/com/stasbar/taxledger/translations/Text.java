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

import com.stasbar.taxledger.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.ListResourceBundle;

import static com.stasbar.taxledger.translations.Text.Actions.*;
import static com.stasbar.taxledger.translations.Text.Exceptions.*;
import static com.stasbar.taxledger.translations.Text.Logger.*;
import static com.stasbar.taxledger.translations.Text.Summary.*;

public class Text extends ListResourceBundle {

    public static final String TOTAL_OPERATIONS = "total_operations";
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
    public static final String LOAD_COMPLETE = "load_complete";
    public static final String NO_OPERATIONS = "no_transactions";
    public static final String AFFILIATE_INCOME = "affiliate_income";
    public static final String CRYPTO_TRANSFER = "crypto_transfer";
    public static final String CARD_WITHDRAW = "card_operation";
    public static final String CANCEL_CARD_WITHDRAW = "cancel_card_withdraw";
    public static final String CARD_ORDER_FEE = "card_order_fee";
    public static final String TRANSFER = "transfer";
    public static final String INVALID_OLDBB_CSV_PATH = "invalid_oldbb_csv_path";
    public static final String COULD_NOT_FIND_PATH_ARGUMENT = "could_not_find_path_parameter";

    public static final String ACTIONS = "actions";
    public static final String TRANSACTIONS_PARAMETER = "transactions_parameter";
    @NotNull
    public static final String UNSUPPORTED = "unsupported";

    public static class Summary {
        public static final String GROSS_INCOME = "gross_income";
        public static final String EXPENSE = "expense";
        public static final String EXPENSE_WITH_FEE = "expense_with_fee";
        public static final String NET_INCOME = "net_income";
        public static final String SUMMARY = "summary";
    }

    public static class Actions {
        public static final String TRANSACTIONS_TITLE = "transactions_title";
        public static final String TRANSACTIONS_DESC = "transactions_desc";
        public static final String OPEN_FOLDER_TITLE = "open_folder_title";
        public static final String OPEN_FOLDER_DESC = "open_folder_desc";
        public static final String CONTACT_TITLE = "contact_title";
        public static final String CONTACT_DESC = "contact_desc";
        public static final String VERSION_TITLE = "version_title";
        public static final String VERSION_DESC = "version_desc";
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
        public static final String DEBUG = "debug";
    }

    public static final String BUY = "buy";
    public static final String SELL = "sell";
    public static final String FEE = "fee";
    public static final String WITHDRAW = "withdraw";
    public static final String DEPOSIT = "deposit";
    public static final String UNKNOWN = "unknown";

    public static class Exceptions {
        public static final String TOO_MANY_ARGS = "too_many_args";
        public static final String CREDENTIALS = "credentials_exc";
        public static final String API_NOT_SET = "api_not_set";
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
            {EXPENSE_WITH_FEE, "Expense (including fees)"},
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
            {DEBUG, "(DEBUG) "},
            {BUY, "Buy"},
            {SELL, "Sell"},
            {FEE, "Fee"},
            {WITHDRAW, "Withdraw"},
            {DEPOSIT, "Deposit"},
            {UNKNOWN, "Unknown"},
            {AFFILIATE_INCOME, "Affiliate Income"},
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
            {NO_OPERATIONS, "No operations found"},
            {TOTAL_OPERATIONS, "Total %d operations"},
            {CRYPTO_TRANSFER, "Crypto transfer"},
            {CARD_WITHDRAW, "Card withdraw"},
            {CANCEL_CARD_WITHDRAW, "Cancel card withdraw"},
            {CARD_ORDER_FEE, "Card order fee"},
            {TRANSFER, "Transfer"},
            {INVALID_OLDBB_CSV_PATH, "%s is invalid .csv path"},
            {COULD_NOT_FIND_PATH_ARGUMENT, "Could not find path/to/old/bitbay/histories.csv argument for -oldbb parameter. It should look like: transactions -oldbb C:\\Users\\username\\Downloads\\report_2018-01-09 14-38-05.csv"},
            {ACTIONS, "Actions"},
            {TRANSACTIONS_PARAMETER, "Transactions parameters"},
            {OPEN_FOLDER_TITLE, "Open"},
            {OPEN_FOLDER_DESC, "Open folder location"},
            {VERSION_TITLE, "Version"},
            {VERSION_DESC, 'v' + Misc.INSTANCE.getVersion()},
            {UNSUPPORTED, "supported"},


    };
}
