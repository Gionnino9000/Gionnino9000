<h1 align="center">Setup</h1>

### Setup Macchina Virtuale
1. Scaricare l'immagine .ova da [questo link](https://liveunibo-my.sharepoint.com/:u:/g/personal/andrea_giovine_unibo_it/Eb_-2bR2YNtAs_F7D2i8jFkBY0KWWKfjNIY4-AoGMwVHFA)
2. Importare ed eseguire la VM
3. Accedere con username `tablut` e password: `tablut`
4. Installare Java 11
  ```bash
  sudo apt install openjdk-11-jdk -y
  ```
5. Installare Ant
  ```bash
  sudo apt install ant -y
  ```

### Setup su IntelliJ
1. Importare la cartella `Tablut/src/` su Intellij (`File` > `Open`)
2. Andare su `File` > `Project Structure` > `Libraries` e selezionare `+`
3. Selezionare `Java` e importare la cartella `Tablut/lib`

Può essere necessario dover impostare "src" come root directory del progetto:
1. Click destro su `src` > `Mark Directory as` > `Sources Root`


### Esecuzione su IntelliJ
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

### Build JAR su IntelliJ
Per compilare il progetto ed ottenere un jar eseguibile, è necessario creare un artefatto:
1. `File` > `Project Structure`
2. `(Project Settings) Artifacts` > `+`
3. `JAR` > `From modules with dependencies`
4. Selezionare la classe del Main che verrà eseguita
