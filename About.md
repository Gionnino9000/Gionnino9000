<h1 align="center">Informazioni</h1>

### Regole di Gioco
<img align="right" width="280" height="280" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/board2.png">

**_Tablut_** è un antico gioco da tavolo di origini nordiche, la cui storia non è del tutto certa.
Esistono diverse regole, ma quelle descritte in questa pagina, chiamate anche "Regole di Ashton", sono le regole adottate per la challenge.

Il tavolo *(eheh tavoletta)* da gioco è costituito da una griglia 9x9. Si gioca in 2 ed i giocatori si alternano muovendo una pedina in ciascun turno. In particolare:
- il **difensore** (bianco), possiede 8 pedine bianche <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/White.png"/>
ed 1 re <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/ImmagineRe.png"/>.
Deve proteggere il re, aiutandolo a fuggire;
- l'**attaccante** (nero), possiede 16 pedine nere <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/Black2.png"/>.
Deve assediare il castello, catturando il re e impedendogli di scappare.

La griglia di gioco comprende delle **caselle speciali**:
- i **campi** <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/camp.png"/>, 
sono le posizioni di partenza delle pedine nere, su cui si possono muovere esclusivamente loro. Se una pedina esce da un campo, poi non può più rientrare;
- il **castello** <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/castle.png"/>,
è la posizione di partenza del re. Come per i campi, solo il re può trovarsi in questa casella, ma se esce non può più rientrare;
- le **vie di fuga** <img width="25" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/escape.png"/>.
Se il re si muove su una di queste caselle, il bianco vince.

<img align="right" width="280" height="280" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/Tablut/src/it/unibo/ai/didattica/competition/tablut/gui/resources/board2.png"/>

**Movimenti** possibili:
- le pedine si muovono ortogonalmente (ovvero non possono fare mosse in diagonale), di un numero qualsiasi di caselle, ma non possono scavalcare i campi ed il castello, o altre pedine;
- le pedine nere si possono muovere liberamente all'interno dello stesso campo, finché non escono, dopodiché non potranno più rientrare.

Le pedine possono essere **catturate**:
- una pedina bianca o nera viene catturata se l'avversario la circonda con due pedine su lati opposti;
- il re può essere catturato solo se viene circondato su ogni lato;
- è possibile catturare più di una pedina alla volta e la cattura dev'essere "attiva": se una pedina si sposta fra due pedine avversarie non viene catturata.

Esistono anche **casi speciali** di catture:
- una pedina bianca o nera può venire catturata anche se si trova in una casella adiacente ad un campo o al castello, e una pedina avversaria si sposta sulla casella opposta 
(i campi ed il castello si comportano come una sorta di "barriera", non importa se tale casella è occupata oppure no);
- se il re si trova in una casella adiacente ad uno o più campi o al castello, è sufficiente circondarlo in tutti i restanti lati liberi per catturarlo.

**Inizio** del gioco: il bianco muove per primo.

Il gioco **termina** quando:
- il re raggiunge una via di fuga, in questo caso <ins>vince il bianco</ins>;
- il re viene catturato, in questo caso <ins>vince il nero</ins>;
- un giocatore non può più muovere pedine, in questo caso tale giocatore perde;
- lo stato di gioco si ripete due volte, in questo caso si ha un pareggio.
 
### Regole della Competizione
La competizione si svolge come un **torneo**: si formano dei team che vengono divisi in gironi in base al numero di partecipanti per squadra (1-4).
Ciascun team gioca contro ciascun altro team del proprio girone, svolgendo una partita come difensore (bianco) ed una come attaccante (nero).<br/>
Per ogni vittoria il team guadagna **3 punti**, mentre in caso di pareggio si ottiene **1 punto**. I giocatori hanno un tempo limitato (pari a **1 minuto**) per decidere quale mossa fare.
Al termine di questo, se il giocatore non ha ancora comunicato la propria mossa al server, perde.

Gli studenti devono <ins>creare un agente software capace di giocare al gioco e comunicare le mosse al server</ins>, su cui esegue il motore di gioco. I messaggi sono formattati come **stringhe JSON**. Il codice del server è fornito dal professor [A. Galassi](https://github.com/AGalassi/TablutCompetition).

### Strategia Adottata
[...]
