![Image](https://i.imgur.com/wBVi8X4.png)
### Tax Ledger
- Jest programem który pozwala na wygenerowanie księgi kosztów i zysków z giełd kryptowalut.  
- Program jest w pełni* przetłumaczony na język Polski. 
- Wybór języka jest podejmowany na podstawie języka systemu operacyjnego.
- Każda komenda może być interpretowalna w obu językach
- Każda komenda jest automatycznie uzupełniana przez klawisz Tab (np. tra -> TAB -> transakcje)
- Program jest w pełni argumentowalny np. `java -jar tax-ledger.jar transactions -lastMonth -onlyBitbay exit`) przetworzy transakcje z poprzedniego miesiąca, z giełdy bitbay i zakończy działanie

### Jak uruchomić

1. Pobierz najnowszą wersję programu (tax-ledger.jar) [Pobierz](https://github.com/stasbar/tax-ledger/releases)
2. Upewnij się, że masz zainstalowaną Javę [Pobierz](https://www.java.com/pl/download/)
3. Otwórz konsolę/terminal [Windows - cmd.exe][macOS - Terminal.app]
4. Wpisz `java -jar ` przeciągnij na okno terminala plik `tax-ledger[version].jar` i wykonaj komendę Enterem

```sh
$ java -jar /Users/stasbar/Downloads/tax-ledger0.1.jar 
```   

#### Dodawanie giełdy
1. Pierwsze uruchomienie poprosi cię o skonfigurowanie pierwszej giełdy
2. Klucze API wygenerujesz w ustawieniach konta. Program wymaga jedynie pozwolenia do odczytu histori transakcji.
3. Aby dodać kolejne giełdy 
- wybierz akcję `giełdy` a następnie skonfiguruj kolejną giełdę
- dodaj po koleji nazwę giełdy a następnie klucze do pliku credentials.txt (Zostanie utworzony po dodaniu pierwszej giełdy)

### Transactions / Transakcje

Podstawowa komenda `transakcje` łączy się z każdą skonfigurowaną giełdą a następnie:
 - pobiera pełną listę transakcjii. Możliwe ograniczenia do -lastWeek -thisWeek -lastMonth -thisMonth -thisYear -lastYear
 - Wyświetla podsumowanie
 - Zapisuje do pliku .csv
  



 

### License
```

Copyright 2017 Stanislaw stasbar Baranski

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


         __             __              
   _____/ /_____ ______/ /_  ____ ______
  / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 (__  ) /_/ /_/ (__  ) /_/ / /_/ / /    
/____/\__/\__,_/____/_.___/\__,_/_/     
           taxledger@stasbar.com            
```