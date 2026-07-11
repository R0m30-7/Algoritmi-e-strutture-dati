# 📝 GENERATORE E RISOLUTORE DI LABIRINTI

Questo progetto è stato sviluppato per l'esame di **Algoritmi e Strutture Dati**. Include l'implementazione procedurale di 7 algoritmi di generazione di labirinti perfetti (Spanning Tree casuali), un risolutore automatico basato su ricerca in ampiezza (BFS) e un'infrastruttura di benchmark per l'analisi dei tempi di esecuzione.

---

## Struttura del progetto

Una volta decompresso l'archivio della consegna, la disposizione dei file si presenta nel seguente modo:

```text
[Cartella Principale]
 ├── TesinaMiscioAlgoritmi.pdf      # Elaborato testuale finale in formato PDF
 ├── README.md                      # Questo file con le istruzioni d'uso
 └── codice/                        # Pacchetto sorgente Java
      ├── algoritmi/                # Classi specifiche dei 7 algoritmi di generazione
      ├── Cell.java                 # Rappresentazione della singola cella della griglia
      ├── ...
      ├── MazeVisualizer.java       # Interfaccia Grafica (GUI) e Risolutore
      └── MazeBenchmark.java        # Infrastruttura di profilazione e Warm-up JIT
```

---

## Requisiti di sistema

- **Java Development Kit (JDK):** Versione 17 o superiore (consigliata versione 26) installata nel sistema e configurata nelle variabili d'ambiente (`javac` e `java` accessibili da terminale).

---

## Istruzioni per la compilazione e l'esecuzione

Per testare il software, aprire una shell di comando e posizionarsi nella **cartella principale** del progetto (la directory che contiene il file `TesinaMiscioAlgoritmi.pdf` e la cartella `codice/`).

### 1. Compilazione dei file di java

Lanciare il seguente comando per compilare simultaneamente tutte le classi del pacchetto principale e del sotto-pacchetto degli algoritmi:

```bash
javac codice/*.java codice/algoritmi/*.java
```

### 2. Esecuzione dell'interfaccia grafica (visualizzatore)

Per lanciare l'applicazione desktop interattiva, che permette di selezionare gli algoritmi, impostare dimensioni dinamiche delle griglie (X e Y), osservare l'animazione a tempo controllato (si punta ad avere 60fps stabili) e visualizzare la risoluzione del percorso, eseguire:

```bash
java codice.MazeVisualizer
```

### 3. Esecuzione del benchmark delle prestazioni

Per avviare il file di benchmark con verifica preventiva di correttezza e cicli di warm-up per il JIT compiler della Java Virtual Machine, eseguire:

```bash
java codice.MazeBenchmark
```

_Il benchmark stamperà direttamente a schermo la tabella comparativa finale dei tempi medi di esecuzione (espressi in millisecondi) calcolati su griglie di dimensioni crescenti da 10x10 a 100x100._
