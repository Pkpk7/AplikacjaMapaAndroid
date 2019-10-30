# AplikacjaMapaAndroid - Mój Rzeszów
## Opis 
Aplikacja pozwalająca zwiedzić miasto Rzeszów, według tras wyznaczonych przez twórce aplikacji. Pozwala ona na śledzenie tego gdzie się było, wyznaczania jednej z wybranych tras w aplikacji, wyświetlania informacji po zbliżeniu się do danego obiektu, pokazywania korków/wypadków na drodzę by pozwolić użytkownikowi na wybranie lepszej trasy. 

![Alt text](app/screeny_dokum/aplikacja_na_ekranie.png?raw=true "Aplikacja Na Telefonie")

Po kliknięciu na aplikację zobaczymy splash screen, który utrzyma się na ekranie aż do załadowania się aplikacji.

![Alt text](app/screeny_dokum/splash_art.png?raw=true "Splash Art")

Po załadowaniu się aplikacji, wyświetlona zostanie mapa która przybliży się automatycznie na Rzeszów. Na mapie zostaną wyświetlone markery które ukazują nam miejsca gdzie można coś zjeść, bary i miejsca warte zobaczenia w Rzeszowie. 

![Alt text](app/screeny_dokum/widok_mapy.png?raw=true "Splash Art")

W lewym górnym rogu mamy cztery przyciski. Pierwsze trzy z góry pozwalają nam na wyznaczenie trasy pomiędzy obiektami tej samej klasy (np pomiędzy wszystkimi miejscami wartymi zobaczenia). Ostatni przycisk pozwala nam włączyć zaawansowaną mapę, na której widać korki, przepustowość dróg, wypadki itp.

![Alt text](app/screeny_dokum/pokaz_trase.png?raw=true "Splash Art")

Widok po kliknięciu w przycisk miejsc wartych do zobaczenia

![Alt text](app/screeny_dokum/pokaz_trase_2.png?raw=true "Splash Art")

Widok po kliknięciu w przycisk miejsc, w których można coś zjeść. 

![Alt text](app/screeny_dokum/pokaz_natezenie_ruchu.png?raw=true "Splash Art")

Widok po kliknięciu w przycisk zaawansowanej mapy.

W każdy z markerów na mapie możemy kliknąć. Po kliknięciu na marker wyświetli się nam chmurka, w której znajdziemy informację na temat nazwy miejsca, a także krótki jego opis. 

![Alt text](app/screeny_dokum/klikniecie_w_obiekt.png?raw=true "Splash Art")

Podczas poruszania się, mapa automatycznie rysuje trasę którą się poruszamy (użytkownik jest oznaczony jako pomarańczowy krążek, trasa jest tego samego koloru)

![Alt text](app/screeny_dokum/rysowanie_trasy.png?raw=true "Splash Art")

Aplikacja korzysta także z geofencingu. Gdy zbliżymy się do jakiegokolwiek z markerków na odpowiednią odległość, wyświetli się nam odpowiedni komunikat, który pokazuje w pobliżu jakich obiektów jesteśmy. Komunikat zamykamy poprzez kliknięcie w niego. 

![Alt text](app/screeny_dokum/poblize_uniwersytet.png?raw=true "Splash Art")

Do wykonania aplikacji użyte zostały Java-Android/TomTom API.

Model biznesowy: 

![Alt text](app/mojRzesz-1.jpg?raw=true "Splash Art")
