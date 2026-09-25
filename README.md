# School API

REST API do zarządzania studentami i nauczycielami, powiązanymi relacją wiele-do-wielu.
Projekt zaliczeniowy.

## Technologie

| | |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Baza danych | H2 (w pamięci) |
| Warstwa dostępu do danych | Spring Data JPA / Hibernate |
| Walidacja | Jakarta Bean Validation |
| Dokumentacja API | springdoc-openapi 3.1.0 (Swagger UI) |
| Build | Gradle |

## Uruchomienie

```bash
./gradlew bootRun
```

Aplikacja startuje na porcie **8081**. Port można nadpisać bez zmiany kodu:

```bash
./gradlew bootRun --args='--server.port=8090'
```

### Konsola H2

Podgląd bazy: `http://localhost:8081/h2-console`

| Pole | Wartość |
|---|---|
| JDBC URL | `jdbc:h2:mem:school` |
| User | `user` |
| Password | `password` |

Baza żyje w pamięci — dane znikają po zatrzymaniu aplikacji.

### Dokumentacja API

| Adres | Zawartość |
|---|---|
| `http://localhost:8081/swagger-ui.html` | Swagger UI — interaktywna lista endpointów |
| `http://localhost:8081/v3/api-docs` | Specyfikacja OpenAPI w formacie JSON |

Dokumentacja powstaje automatycznie z kontrolerów, typów DTO i adnotacji
walidacyjnych — ograniczenia w rodzaju `@Min(19)` czy `@NotBlank` są widoczne
w schematach żądań jako `minimum` i pola wymagane.

Wersja `springdoc-openapi` jest wpisana jawnie, ponieważ biblioteka nie należy do
zestawu zarządzanego przez Spring Boot. Wersja `3.x` odpowiada Spring Bootowi 4;
starsza linia `2.x` jest przeznaczona dla Spring Boota 3 i nie zadziała.

## Struktura projektu

```
org.example.school
├── controller    — obsługa HTTP, przyjmuje i zwraca wyłącznie DTO
├── service       — logika biznesowa, transakcje, mapowanie DTO ↔ encja
├── repository    — dostęp do bazy (interfejsy Spring Data)
├── model         — encje JPA i typy domenowe
├── dto           — obiekty kontraktu API (rekordy)
└── exception     — własne wyjątki i globalna obsługa błędów
```

## Model danych

```
teachers ──┐                          ┌── students
           └── teachers_students ─────┘
               PK (teacher_id, student_id)
```

Tabela łącząca ma klucz główny złożony z pary identyfikatorów — dzięki temu baza
sama gwarantuje, że tego samego nauczyciela nie da się przypisać studentowi dwukrotnie.

## API

### Nauczyciele

| Metoda | Ścieżka | Opis |
|---|---|---|
| `POST` | `/api/teachers` | Utworzenie nauczyciela (201) |
| `GET` | `/api/teachers` | Lista nauczycieli (stronicowana) |
| `GET` | `/api/teachers/search` | Wyszukiwanie po imieniu i nazwisku |
| `GET` | `/api/teachers/{teacherId}/students` | Studenci danego nauczyciela |
| `PUT` | `/api/teachers/{id}` | Edycja nauczyciela |
| `DELETE` | `/api/teachers/{id}` | Usunięcie nauczyciela (204) |
| `PUT` | `/api/teachers/{teacherId}/students/{studentId}` | Przypisanie studenta |
| `DELETE` | `/api/teachers/{teacherId}/students/{studentId}` | Odpięcie studenta |

### Studenci

| Metoda | Ścieżka | Opis |
|---|---|---|
| `POST` | `/api/students` | Utworzenie studenta (201) |
| `GET` | `/api/students` | Lista studentów (stronicowana) |
| `GET` | `/api/students/search` | Wyszukiwanie po imieniu i nazwisku |
| `GET` | `/api/students/{studentId}/teachers` | Nauczyciele danego studenta |
| `PUT` | `/api/students/{id}` | Edycja studenta |
| `DELETE` | `/api/students/{id}` | Usunięcie studenta (204) |
| `PUT` | `/api/students/{studentId}/teachers/{teacherId}` | Przypisanie nauczyciela |
| `DELETE` | `/api/students/{studentId}/teachers/{teacherId}` | Odpięcie nauczyciela |

### Przykład

```bash
curl -X POST http://localhost:8081/api/teachers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Anna","lastName":"Kowalska","age":35,
       "email":"anna@example.com","subject":"MATEMATYKA"}'
```

### Stronicowanie i sortowanie

Endpointy listujące i wyszukujące zwracają stronę wyników — obiekt z tablicą
`content` i metadanymi (`totalElements`, `totalPages`, `number`, `size`),
a nie samą tablicę.

| Parametr | Znaczenie | Domyślnie |
|---|---|---|
| `page` | numer strony, liczony od zera | `0` |
| `size` | liczba elementów na stronie | `20` |
| `sort` | pole i kierunek, np. `lastName,desc` | `lastName,firstName` rosnąco |

```
GET /api/students?page=1&size=5
GET /api/students?sort=lastName,desc&sort=firstName,asc
```

### Wyszukiwanie

Oba parametry są opcjonalne i działają na zasadzie dopasowania częściowego,
bez rozróżniania wielkości liter. Pominięcie parametru oznacza brak filtrowania
po tym polu, więc jeden endpoint obsługuje wszystkie kombinacje.

```
GET /api/students/search                            → wszyscy
GET /api/students/search?lastName=kow               → po fragmencie nazwiska
GET /api/students/search?firstName=an&lastName=ko   → po obu polach
GET /api/students/search?lastName=kow&size=5        → łącznie ze stronicowaniem
```

## Walidacja

| Pole | Reguła |
|---|---|
| `firstName`, `lastName` | niepuste, minimum 3 znaki |
| `email` | niepusty, poprawny format |
| `age` | wymagany, minimum 19 |
| `subject` / `fieldOfStudy` | wymagane |

Błędy walidacji zwracane są jako `400` z mapą `pole → komunikat`.
Nieistniejący identyfikator daje `404`.

## Decyzje projektowe

### Nazewnictwo po angielsku

Mimo polskiej treści zadania klasy i API są po angielsku — frameworki, adnotacje
i mechanizm generowania zapytań z nazw metod repozytoriów operują po angielsku,
więc mieszanie języków dawałoby hybrydy w rodzaju `findAllByImieContaining`.
Warstwa prezentacji (front) odpowiada za tłumaczenie na język użytkownika.

### Klucz główny: `Long` + `IDENTITY`

Rozważono UUID — daje nieprzewidywalne identyfikatory (utrudnia odgadywanie
adresów zasobów) i brak kolizji przy scalaniu baz. Odrzucono, bo losowy klucz
pogarsza lokalność indeksu, a temat zadania dotyczy relacji i REST, nie strategii
kluczy. `Long` zamiast `Integer`, ponieważ późniejsza zmiana typu klucza na żywej
bazie jest kosztowna, a nadmiar zakresu nie kosztuje nic.

### `Subject` jako enum, `fieldOfStudy` jako tekst

Przedmiotów jest kilkanaście i zbiór jest stabilny — enum daje kontrolę
kompilatora i wyklucza literówki w danych. Kierunków studiów są setki, powstają
nowe, więc zamknięty zbiór w kodzie byłby ograniczeniem, a nie pomocą.

Enum zapisywany jest jako **tekst** (`EnumType.STRING`), nie jako liczba porządkowa.
Zapis liczbowy powodowałby ciche przekłamanie danych przy dodaniu nowej wartości
w środku listy.

### Wiek zamiast daty urodzenia

Zadanie wymaga pola „wiek", więc tak zostało zaimplementowane. Docelowo lepsza
byłaby data urodzenia: wiek jest daną pochodną, która dezaktualizuje się sama
wraz z upływem czasu, a data urodzenia jest faktem niezmiennym.

### DTO oddzielone od encji

Kontrolery nie widzą encji — przyjmują i zwracają wyłącznie DTO. Powody:

1. Encja odzwierciedla schemat bazy, DTO jest kontraktem z klientem; jedno nie
   powinno wymuszać zmian w drugim.
2. Encja może zawierać pola, których nie należy wystawiać na zewnątrz.
3. Relacja dwukierunkowa tworzy cykl (student → nauczyciele → studenci → …),
   który przy serializacji do JSON zakończyłby się `StackOverflowError`.

Cykl przerywają **DTO podsumowujące** (`TeacherSummary`, `StudentSummary`) —
zawierają identyfikator, imię i nazwisko, ale już żadnych kolekcji.

### Relacja wiele-do-wielu

- **Właścicielem relacji jest `Teacher`** — po tej stronie `@JoinTable`, po stronie
  `Student` `mappedBy`. Hibernate zapisuje do tabeli łączącej wyłącznie zmiany
  kolekcji właściciela.
- **Kolekcje typu `Set`** — przypisanie albo istnieje, albo nie; kolejność nie niesie
  informacji, a duplikat naruszałby klucz główny tabeli łączącej. `Set` pozwala też
  Hibernate usunąć pojedyncze powiązanie zamiast przepisywać całą kolekcję.
- **Metody pomocnicze w encjach** (`addStudent` / `removeStudent` oraz delegujące
  `addTeacher` / `removeTeacher`) aktualizują obie strony relacji naraz. Dzięki
  umieszczeniu ich w encji, a nie w serwisie, nie da się o tej synchronizacji
  zapomnieć. Z tego samego powodu kolekcje nie mają setterów.
- **Bez kaskadowania usuwania.** Student i nauczyciel istnieją niezależnie —
  usunięcie studenta ma zerwać powiązanie, a nie skasować nauczycieli.
- **`equals()` i `hashCode()`** oparte na identyfikatorze, z `hashCode()` zwracającym
  wartość stałą. Identyfikator nadawany jest przy zapisie, więc `hashCode()` liczony
  z niego zmieniałby się w trakcie życia obiektu — a obiekt już umieszczony w `Set`
  stałby się w nim nieodnajdywalny.

### Usuwanie a tabela łącząca

Usuwanie zachowuje się niesymetrycznie i wymaga to jawnej obsługi:

- **Nauczyciel** jest właścicielem relacji, więc Hibernate sam kasuje jego wiersze
  w tabeli łączącej przed usunięciem encji.
- **Student** jest stroną `mappedBy`, której Hibernate nie zapisuje — samo
  `delete()` skończyłoby się naruszeniem klucza obcego (`500`). Dlatego przed
  usunięciem studenta kod odpina go od wszystkich nauczycieli.

Pętla odpinająca działa na **kopii** kolekcji — modyfikowanie zbioru w trakcie
iterowania po nim rzuciłoby `ConcurrentModificationException`.

Alternatywą byłaby reguła `ON DELETE CASCADE` na kluczu obcym, ale wtedy logika
integralności wyciekłaby do schematu bazy i nie byłaby widoczna w kodzie.

### Problem N+1 i `@EntityGraph`

Kolekcje `@ManyToMany` ładują się leniwie, więc mapowanie strony wyników na DTO
sięgało po kolekcję osobno dla każdego elementu — jedno zapytanie o dane strony
plus po jednym na każdy rekord. Dla dziesięciu studentów dawało to 12 zapytań,
dla stu — 102.

Metody repozytoriów zwracające listy mają więc `@EntityGraph`, który dociąga
kolekcję w tym samym zapytaniu. Liczba zapytań spadła do dwóch (dane strony
i zliczenie) i **nie zależy już od liczby zwracanych rekordów**.

Dotyczy to również metod filtrujących po relacji: złączenie użyte w warunku
`WHERE` służy do odsiania wyników i nie inicjalizuje kolekcji, więc bez tej
adnotacji problem występowałby tam tak samo.

Hibernate stronicuje przy tym poprawnie — nakłada `OFFSET`/`FETCH` w podzapytaniu
na samą encję główną, a złączenie z kolekcją wykonuje na zewnątrz. Starsze wersje
pobierały w takiej sytuacji całą tabelę i stronicowały ją w pamięci aplikacji
(ostrzeżenie `HHH000104`).

### `PUT` zamiast `POST` przy przypisywaniu

Przypisanie nauczyciela do studenta jest idempotentne — kolekcja jest zbiorem,
więc powtórzenie operacji nie zmienia stanu. `PUT` oddaje to wprost, `POST`
oznaczałby tworzenie nowego zasobu przy każdym wywołaniu.

### `@NotBlank` zamiast `@NotNull` dla tekstu

`@NotNull` przepuszcza pusty łańcuch i sam biały znak; `@NotBlank` odrzuca oba.
Dla pól liczbowych i enumów pozostaje `@NotNull`, bo `@NotBlank` dotyczy tekstu.

### Transakcje

Metody modyfikujące relacje są oznaczone `@Transactional`. Encje wczytane w obrębie
transakcji są zarządzane przez Hibernate, który sam wykrywa zmiany i zapisuje je
na jej końcu — dlatego nie ma tam jawnych wywołań `save()`. Transakcja jest też
warunkiem doładowania kolekcji ładowanych leniwie.

### Baza w pamięci

H2 w pamięci wybrano, żeby projekt uruchamiał się bez instalowania czegokolwiek.
Schemat tworzy Hibernate. W projekcie produkcyjnym schematem zarządzałoby
narzędzie migracyjne (Flyway, Liquibase), gdzie każda zmiana jest wersjonowanym
skryptem w repozytorium.

## Do zrobienia

Wszystkie wymagania zadania są zrealizowane. Poniższe punkty to dopracowanie.

### 1. Testy

Razem 43 testy. Każdy z nich został sprawdzony mutacją kodu produkcyjnego —
zepsuciem jednej linii i upewnieniem się, że test faktycznie pada.

**Testy jednostkowe serwisów — zrobione.** `StudentServiceTest` (7) i
`TeacherServiceTest` (10): Mockito, zaślepione repozytoria, bez kontekstu Springa.
Pokrywają `NotFoundException` przy nieistniejącym identyfikatorze, przypisywanie
i odpinanie, usuwanie (łącznie z odpięciem studenta od wszystkich nauczycieli
na kopii kolekcji) oraz to, że edycja nie gubi przypisań.

```bash
./gradlew test
```

Poza zakresem testów jednostkowych zostały:
- metody `save` i wyszukujące — to cienkie przekazania do repozytorium,
  sensowniej sprawdzić je w testach webowych i repozytoriów,
- obustronna synchronizacja relacji w samych encjach (`Teacher.addStudent`,
  `removeStudent`) — testy serwisów sprawdzają ją tylko pośrednio; docelowo
  osobny test encji, bez Mockito i Springa.

**Testy warstwy webowej — zrobione.** `StudentControllerTest` (12)
i `TeacherControllerTest` (13): `@WebMvcTest` z `MockMvcTester`, serwisy
podstawione przez `@MockitoBean`. Pokrywają wszystkie endpointy obu kontrolerów,
kody odpowiedzi i oba kształty błędu z `GlobalExceptionHandler`:

| Scenariusz | Oczekiwanie |
|---|---|
| serwis rzuca `NotFoundException` | `404` z polem `message` |
| `age` poniżej minimum | `400` z mapą `errors` |
| poprawne dane | `201` z pełnym JSON-em zasobu |
| edycja poprawnymi danymi | `200` z pełnym JSON-em zasobu |
| usunięcie | `204` z pustym ciałem |
| niepoprawny JSON | `400` z polem `message` |
| nieznana wartość enuma `subject` (tylko nauczyciele) | `400` z polem `message` |
| lista | `200` z `content`, `totalElements` i `totalPages` |
| żądanie bez parametrów | `Pageable` i puste napisy z wartości domyślnych |

Endpointy zwracające `Page` mają po dwa testy. Pierwszy sprawdza kształt
odpowiedzi na zaślepce zwracającej `PageImpl` z listą dwóch elementów, rozmiarem
strony 2 i sumą 5 — trzy różne liczby, więc pomylenie pól od razu widać. Drugi
nie ogląda odpowiedzi, tylko przez `verify` pilnuje argumentu przekazanego do
serwisu: `PageRequest.of(0, 20, Sort.by(ASC, "lastName", "firstName"))` z
`@PageableDefault` oraz puste napisy z `@RequestParam(defaultValue = "")`.
Do sprawdzenia argumentu użyty jest `verify` z konkretną wartością, a nie
`ArgumentCaptor`: `PageRequest` ma `equals`, więc jedno porównanie obejmuje
numer strony, rozmiar i całe sortowanie.

W teście poprawnej edycji zaślepka zwraca zasób z jednym przypisanym
nauczycielem (u nauczycieli — studentem). Tej wartości nie ma w ciele żądania,
więc odpowiedzi nie da się odtworzyć z tego, co przysłał klient. Bez niej test
przechodził także wtedy, gdy kontroler ignorował wynik serwisu i sklejał
odpowiedź z żądania — sprawdzone mutacją kodu produkcyjnego.

Wymaga osobnej zależności, bo w Spring Boot 4 testy webowe są w oddzielnym
module: `testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'`.

**Testy repozytoriów** — `@DataJpaTest`: czy metody pochodne faktycznie filtrują
tak, jak sugerują ich nazwy (dopasowanie częściowe, ignorowanie wielkości liter,
filtrowanie po relacji). Tutaj także: czy usunięcie nauczyciela kasuje jego
wiersze w tabeli łączącej — test jednostkowy sprawdza tylko wywołanie `delete`,
samo usunięcie wierszy wykonuje Hibernate i widać je dopiero na prawdziwej bazie.

### 2. Dane startowe

`CommandLineRunner` wstawiający kilku nauczycieli, studentów i przypisania między
nimi — z warunkiem wykonania tylko wtedy, gdy baza jest pusta. Ułatwi ręczne
testowanie po każdym restarcie (baza jest w pamięci, więc znika).

### 3. Granice transakcji

Metody `save` w obu serwisach nie mają `@Transactional`, choć mapują wynik na DTO
sięgające po leniwą kolekcję. Działa to wyłącznie dzięki domyślnie włączonemu
`spring.jpa.open-in-view`, które przedłuża sesję Hibernate na czas całego żądania.

Kolejność: najpierw `@Transactional` na `save`, potem
`spring.jpa.open-in-view=false`, potem sprawdzenie wszystkich endpointów pod kątem
`LazyInitializationException`. Zniknie też ostrzeżenie przy starcie aplikacji.

### 4. Drobiazgi

- Rozważyć `spring.jpa.hibernate.ddl-auto=create-drop` zamiast `update` —
  przy bazie w pamięci `update` nie ma nic do aktualizowania i myli intencję.
- Adnotacje `@Tag` i `@Operation` dla czytelniejszej dokumentacji w Swaggerze.
- Ujednolicić język komunikatów błędów — `NotFoundException` zwraca angielski,
  handler `HttpMessageNotReadableException` polski. Zgodnie z decyzją o angielskim
  nazewnictwie tłumaczenie należy do warstwy prezentacji.
- Komunikat przy nieznanej wartości enuma. `"subject": "ASTROLOGIA"` odrzuca już
  Jackson (`HttpMessageNotReadableException`), zanim ruszy walidacja, więc klient
  dostaje ogólne „Nieprawidłowy format żądania” — bez nazwy pola i bez listy
  dozwolonych wartości. Usprawnienie: w handlerze sprawdzić `ex.getCause()`
  i dla `InvalidFormatException` zwrócić pole oraz wartości enuma. Obecne zachowanie
  opisuje test `saveTeacherInvalidSubjectReturns400` — po zmianie trzeba go dostosować.
- `toString()` na encjach (tylko pola proste, bez kolekcji) — komunikaty porażek
  w testach pokazują dziś `Student@119c745c` zamiast danych.
