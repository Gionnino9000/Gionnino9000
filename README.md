<h1 align="center">Gionnino9000</h1>
<div align="center">

  Repository della nostra soluzione per la Tablut Challenge 2022 organizzata per il corso di [Fondamenti di Intelligenza Artificiale M](https://www.unibo.it/it/didattica/insegnamenti/insegnamento/2021/468002).<br/><br/>
  <a href="https://www.youtube.com/watch?v=G2NjmWRps28">Ascolta sto banger</a>
  
</div>

### Spiegazione del Nome
Tablut = Tavoletta<br/>
Tavoletta = l'Amico immaginario di Jonnino in **Ed, Edd & Eddy**<br/>
Jonnino -> Gionnino (assolutamente non perché abbiamo sbagliato a scrivere il nome quando ci siamo iscritti)<br/>
9000 = piccola **nerd reference** a 2001 Odissea nello Spazio, **HAL9000**<br/>
<br/>
*Fine della Spiegazione del Nome*<br/>
<br/>
Quindi in pratica fra, cioè stavamo tipo scegliendo il nome (assurdo cioè non puoi capire), quando all'improvviso mi sono ricordato che quel chad di Danny Antonucci aveva fatto tipo un [masterpiece](https://en.wikipedia.org/wiki/Ed,_Edd_n_Eddy). E allora ho assolutamente dovuto sussare un nome zio, ho dovuto mostrare un po' di drip, no cap my G, only flames 🥶 e ho droppato sto pezzo di nome gigante bro, figata. Perché praticamente vez, noi così siamo il team Gionnino9000, e tipo il nostro player è Tavoletta. Capito bel? Perché gioca a Tablut, troppo figata, sigma grindset, basato fattuale.


### Membri del Team
- [Federico Andrucci](https://github.com/Federicoand98)
- [Karina Chichifoi](https://github.com/TryKatChup)
- [Alex Gianelli](https://github.com/Noesh)
- [Michele Righi](https://github.com/mikyll)

### Team degli Anni Precedenti
[History](https://github.com/Gionnino9000/Gionnino9000/blob/main/history.md)

### Riferimenti
- [Sito Challenge](http://ai.unibo.it/games/boardgamecompetition/tablut)
- [An Upper Bound on the Complexity of Tablut](http://ai.unibo.it/sites/ai.unibo.it/files/Complexity_of_Tablut_2.pdf)

### Setup Macchina Virtuale
1. Scaricare l'immagine .ova da [questo link](https://liveunibo-my.sharepoint.com/:u:/g/personal/andrea_giovine_unibo_it/Eb_-2bR2YNtAs_F7D2i8jFkBY0KWWKfjNIY4-AoGMwVHFA)
2. Importare ed eseguire la VM
3. Accedere con username: "tablut", password: "tablut"
4. Installare Java
  ```bash
  sudo apt install openjdk-11-jdk -y
  ```
5. Installare Ant
  ```bash
  sudo apt install ant -y
  ```

### Setup su IntelliJ
1. Importare la cartella `Tablut/src/` su Intellij (`File` -> `Open`)
2. Andare su `File` -> `Project Structure` -> `Libraries` e selezionare `+`
3. Selezionare `Java` e importare la cartella `Tablut/lib`

### Esecuzione
1. Eseguire `Tablut/src/Server.java`

    Parametri (tutti `int`, tranne `-g`): 

    - `-c,--cache <arg>`          limite alla cache (default: infinito)
    - `-e,--errors <arg>`         numero di errori (default: 0)
    - `-g,--enableGUI`            opzione che abilita la GUI
    - `-r,--game rules <arg>`     versione di Tablut: `1` per Tablut, `2`
                                for Modern, `3` for Brandub, `4` for Ashton
                                (default: 4)
    - `-s,--repeatedState <arg>`  numero di stati ripetuti (default: 0)
    - `-t,--time <arg>`           numero di secondi (default: 60)

2. Eseguire `Tablut/src/it/unibo/ai/didattica/competition/tablut/gionnino9000/clients/BlackTavoletta.java`
3. Eseguire `Tablut/src/it/unibo/ai/didattica/competition/tablut/gionnino9000/clients/WhiteTavoletta.java`
4. Have fun!

### Memoni Giganti
<img width="50%" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/gfx/WidePlank.gif"/>
<img width="50%" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/gfx/GionninoSus.png"/>
<img width="50%" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/gfx/TavolettaPiediGrandi.png"/>
<img width="50%" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/gfx/TavolettaPH.png"/>
<img width="50%" src="https://github.com/Gionnino9000/Gionnino9000/blob/main/gfx/BigPP.png"/>
