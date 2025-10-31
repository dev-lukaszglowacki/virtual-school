# Plan Działania: Projekt Wirtualna Szkoła

## Faza 1: Konfiguracja Środowiska i Backendu

1.  **Uruchomienie Keycloak i PostgreSQL:**
    *   Użyj dostarczonego pliku `docker-compose.yml`, aby uruchomić kontenery za pomocą `docker-compose up -d`.
    *   Zweryfikuj, czy Keycloak jest dostępny pod `http://localhost:8080` i zaloguj się przy użyciu danych uwierzytelniających administratora (`keycloakadmin`/`keycloakadmin`).

2.  **Konfiguracja Keycloak:**
    *   Utwórz nowy realm o nazwie `virtual-school`.
    *   Wewnątrz realmu `virtual-school` utwórz klienta (client) dla aplikacji Spring Boot o nazwie `spring-boot-app`.
        *   Skonfiguruj klienta jako poufnego (confidential) i zanotuj jego `client secret`.
        *   Ustaw prawidłowe `Valid Redirect URIs` (np. `http://localhost:8081/*`).
    *   Utwórz klienta dla aplikacji React o nazwie `react-app`.
        *   Skonfiguruj klienta jako publicznego (public).
        *   Ustaw prawidłowe `Valid Redirect URIs` (np. `http://localhost:3000/*`) oraz `Web Origins` (np. `http://localhost:3000`).
    *   Utwórz role w ramach realmu: `admin`, `teacher`, `student`.
    *   Utwórz przykładowych użytkowników i przypisz im odpowiednie role.

3.  **Inicjalizacja Projektu Spring Boot:**
    *   Użyj Spring Initializr (`start.spring.io`) do wygenerowania nowego projektu.
    *   Dodaj zależności:
        *   `Spring Web`
        *   `Spring Security`
        *   `Spring Data JPA`
        *   `OAuth2 Resource Server`
        *   `PostgreSQL Driver`
        *   `Validation`

4.  **Integracja Spring Boot z Keycloak:**
    *   W pliku `application.properties` (lub `application.yml`) skonfiguruj połączenie z Keycloak jako serwerem zasobów.
        ```properties
        spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/virtual-school
        spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/virtual-school/protocol/openid-connect/certs
        ```
    *   Skonfiguruj `SecurityConfig` w Springu, aby zabezpieczyć endpointy w oparciu o role z Keycloak. Na przykład, endpointy do zarządzania użytkownikami powinny wymagać roli `admin`.

5.  **Implementacja Modeli i Repozytoriów:**
    *   Utwórz encje JPA dla `Student`, `Lecturer` i `Lesson`.
    *   Utwórz odpowiednie repozytoria Spring Data JPA dla każdej encji.

6.  **Implementacja API (CRUD):**
    *   Utwórz kontrolery REST (`StudentController`, `LecturerController`, `LessonController`).
    *   Zaimplementuj endpointy CRUD (Create, Read, Update, Delete) dla studentów, wykładowców i planu lekcji.
    *   Zabezpiecz endpointy za pomocą adnotacji `@PreAuthorize` lub w konfiguracji bezpieczeństwa, np. tylko użytkownicy z rolą `admin` lub `teacher` mogą modyfikować dane.

## Faza 2: Frontend w React

1.  **Inicjalizacja Projektu React:**
    *   Użyj `create-react-app` do stworzenia nowej aplikacji: `npx create-react-app virtual-school-ui`.

2.  **Integracja z Keycloak:**
    *   Dodaj bibliotekę `keycloak-js`.
    *   Skonfiguruj klienta Keycloak w aplikacji React, aby zarządzał logowaniem i uwierzytelnianiem użytkowników. Token JWT uzyskany po zalogowaniu będzie wysyłany w nagłówku `Authorization` każdego żądania do backendu.

3.  **Struktura Komponentów:**
    *   Utwórz komponenty do wyświetlania i zarządzania:
        *   `StudentList`, `StudentForm`
        *   `LecturerList`, `LecturerForm`
        *   `ScheduleView`, `LessonForm`
    *   Zaimplementuj routing (np. przy użyciu `react-router-dom`) do nawigacji między widokami.

4.  **Komunikacja z API:**
    *   Użyj `axios` lub `fetch` do wysyłania żądań do API Spring Boot.
    *   Dołączaj token JWT do każdego żądania, aby autoryzować operacje.

5.  **Zarządzanie Stanem:**
    *   Wykorzystaj `useState`, `useEffect` lub bibliotekę do zarządzania stanem (np. Redux, Zustand) do obsługi danych pobieranych z API.

6.  **Implementacja Interfejsu Użytkownika:**
    *   Stwórz formularze do dodawania i edycji studentów, wykładowców i lekcji.
    *   Zaimplementuj widoki listujące dane z opcjami sortowania i filtrowania.
    *   Dodaj przyciski do usuwania poszczególnych rekordów.
    *   Zabezpiecz widoki i funkcjonalności w oparciu o rolę zalogowanego użytkownika (np. przycisk "Dodaj studenta" widoczny tylko dla admina).

## Faza 3: Testowanie i Wdrożenie

1.  **Testowanie:**
    *   Napisz testy jednostkowe i integracyjne dla logiki biznesowej w Spring Boot.
    *   Przetestuj działanie API za pomocą narzędzi takich jak Postman lub Insomnia.
    *   Przeprowadź testy manualne interfejsu użytkownika, sprawdzając wszystkie ścieżki i uprawnienia.

2.  **Wdrożenie:**
    *   Skonfiguruj środowisko produkcyjne (np. osobna baza danych, inne ustawienia Keycloak).
    *   Zbuduj aplikację Spring Boot do pliku JAR.
    *   Zbuduj statyczne pliki aplikacji React.
    *   Wdróż aplikację backendową i serwuj pliki frontendu (np. za pomocą Nginx lub serwera aplikacyjnego).
    *   Zaktualizuj konfigurację `docker-compose` o usługi aplikacji, jeśli jest to pożądane.
