![Image](https://i.imgur.com/wBVi8X4.png)
### Tax Ledger
Jest programem który pozwala na wygenerowanie zestawienia kosztów i przychodów z giełd kryptowalut.  

[![TAX LEDGER DEMO](https://img.youtube.com/vi/9FUEPTlwf4w/0.jpg)](https://www.youtube.com/watch?v=9FUEPTlwf4w)


### Jak uruchomić

##### Windows
1. Upewnij się, że masz zainstalowaną Javę [Pobierz](https://www.java.com/pl/download/)
2. Pobierz i uruchom najnowszą wersję programu [tax-ledger.exe](https://github.com/stasbar/tax-ledger-cli/releases/download/v1.1.2/tax-ledger-windows.exe)
##### macOS
###### metoda 1 Polecana
1. Upewnij się, że masz zainstalowaną Javę [Pobierz](https://www.java.com/pl/download/)
2. Pobierz i zamontuj obraz [tax-ledger-macOS.dmg](https://github.com/stasbar/tax-ledger-cli/releases/download/v1.1.2/tax-ledger-macOS.dmg)
3. Przeciągnij aplikację (Tax Ledger.app) na skrót folderu Applocations 
###### metoda 2 i Linux
1. Upewnij się, że masz zainstalowaną Javę [Pobierz](https://www.java.com/pl/download/)
2. Pobierz plik [tax-ledger.jar](https://github.com/stasbar/tax-ledger-cli/releases/download/v1.1.2/tax-ledger.jar)
3. Otwórz terminal [macOS - Terminal.app] w miejsu pobranego pliku 
4. Włącz program poleceniem

```sh
$ java -jar tax-ledger.jar 
```   

#### Dodawanie giełdy
1. Pierwsze uruchomienie poprosi cię o skonfigurowanie pierwszej giełdy
2. Klucze API wygenerujesz w ustawieniach konta. Program wymaga jedynie pozwolenia do odczytu histori transakcji.
3. Aby dodać kolejne giełdy 
- wybierz akcję `gieldy` a następnie skonfiguruj kolejną giełdę
- dodaj po koleji nazwę giełdy a następnie klucze do pliku credentials.txt (Zostanie utworzony po dodaniu pierwszej giełdy)

### Transactions / Transakcje

Podstawowa komenda `transakcje` łączy się z każdą skonfigurowaną giełdą a następnie:
 - pobiera pełną listę transakcjii.  
 - Wyświetla podsumowanie
 - Zapisuje wyniki do pliku [nazwa komendy].csv w folderze /transactions
  
 Dostępne argumenty dla komendy `transactions`:
 - `--today`, `--yesterday`, `--thisYear`, `--prevYear`, `--thisMonth` lub `--prevMonth`
 - lub bezpośrednie określenie daty poprzez np. `--date 2017`, `--data 12.2017` lub `--date 28.12.2017`
 - jednostronne zakresy dat przez `--after 8.2017`, `--po 8.2017`, `--before 11.2017`  `--przed 11.2017`  
 - `--reverse` odwrócenie listy wyników 
 - `--onlyBB` wyświetla wyniki tylko dla BitBay analogicznie `-onlyAbu` dla Abucoins
 - `--showNonFiat` wyświetla również operacje crypto-crypto. Domyślnie wyłączone ponieważ do celów podatkowych jest to zbędna informacja.
 - `--showNonEssential` wyświetla również wpłaty i wypłaty PLNów na konto giełdowe. Domyślnie wyłączone ponieważ do celów podatkowych jest to zbędna informacja.
 - `--all` wyświetla wszystkie transakcje 
 ![GIF](https://i.imgur.com/8ctkN6g.gif)
 
### Przykłady
W celu rozlicznia dochodów za rok 2017
`transakcje --date 2017`
W celu rozliczenia dochodów za poprzedni miesiąc
`trankakcje --lastMonth`
W celu rozliczenia dochodów za styczeń 2018
`transakcje --date 01.2018`
W celu rozliczenia dochodów za pierwszy kwartał
`tranksakcje --after 01.2017 --before 03.2017`

### Informacje
- Program jest w pełni* przetłumaczony na język Polski. 
- Wybór języka jest podejmowany na podstawie języka systemu operacyjnego.
- Każda komenda może być interpretowalna w obu językach
- Każda komenda jest automatycznie uzupełniana przez klawisz Tab (np. tra -> TAB -> transakcje)
- Program jest w pełni argumentowalny np. `java -jar tax-ledger.jar transactions -lastMonth -onlyBitbay exit`) przetworzy transakcje z poprzedniego miesiąca, z giełdy bitbay i zakończy działanie

### Znane problemy
- Abucoins nie pozwala na pobranie historii wpłat i wypłat. Aktualny stan: Oczekiwanie na poprawę przez Abucoins 

### Do zrobienia
- Stworzenie GUI
- Podsumowanie wszystkich walut na giełdach 
- Automatyczne generowanie porfolio(zysk/strata) na podstawie histori transakcji z giełd

## Budowanie ze źródła
W celu zbudowania wykonywalnego pliku .jar przejdź do folderu projektu i wykonaj polecenie
`./gradlew shadowJar`
wykonywalny plik .jar zawierający wszystkie zależności, zostanie wygenerowany do folderu /build/libs/  

### Licencje

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