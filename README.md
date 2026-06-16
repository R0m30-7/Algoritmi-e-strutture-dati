# 📝 GENERATORE E RISOLUTORE DI LABIRINTI (ASD)

Questo progetto software è stato sviluppato per l'esame di **Algoritmi e Strutture Dati**. Include l'implementazione procedurale di 7 algoritmi di generazione di labirinti perfetti (Spanning Tree casuali), un risolutore automatico basato su ricerca in ampiezza (BFS) e un'infrastruttura di benchmark headless per l'analisi dei tempi di esecuzione.

---

## 📂 Struttura del Progetto

Una volta decompresso l'archivio della consegna, la disposizione dei file si presenta nel seguente modo:

```text
[Cartella Principale]
 ├── tesina.pdf           # Elaborato testuale finale in formato PDF
 ├── README.md            # Questo file con le istruzioni d'uso
 └── codice/              # Pacchetto sorgente Java
      ├── algoritmi/      # Classi specifiche dei 7 algoritmi di generazione
      ├── Cell.java       # Rappresentazione della singola cella della griglia
      ├── ...
      ├── MazeVisualizer.java  # Interfaccia Grafica (GUI) e Risolutore
      └── MazeBenchmark.java   # Infrastruttura di profilazione e Warm-up JIT
```

---

## 🛠️ Requisiti di Sistema

- **Java Development Kit (JDK):** Versione 17 o superiore (consigliata versione 26) installata nel sistema e configurata nelle variabili d'ambiente (`javac` e `java` accessibili da terminale).

---

## 💻 Istruzioni per la Compilazione e l'Esecuzione

Per testare il software, apri una shell di comando (Terminale, Prompt dei comandi o PowerShell) e posizionati nella **cartella principale** del progetto (la directory che contiene il file `tesina.pdf` e la cartella `codice/`).

### 1. Compilazione dei Sorgenti

Lancia il seguente comando per compilare simultaneamente tutte le classi del pacchetto principale e del sotto-pacchetto degli algoritmi:

```bash
javac codice/*.java codice/algoritmi/*.java
```

_Se il comando viene eseguito correttamente, verranno generati i rispettivi file `.class` all'interno delle cartelle dei sorgenti senza mostrare alcun messaggio di errore._

### 2. Esecuzione dell'Interfaccia Grafica (Visualizzatore)

Per lanciare l'applicazione desktop interattiva, che permette di selezionare gli algoritmi, impostare dimensioni dinamiche delle griglie (X e Y), osservare l'animazione a tempo controllato (max 15s) e visualizzare la risoluzione del percorso, esegui:

```bash
java codice.MazeVisualizer
```

### 3. Esecuzione del Benchmark delle Prestazioni

Per avviare la profilazione headless strutturata con verifica preventiva di correttezza e cicli di warm-up per il JIT compiler della Java Virtual Machine, esegui:

```bash
java codice.MazeBenchmark
```

_Il benchmark stamperà direttamente a schermo la tabella comparativa finale dei tempi medi di esecuzione (espressi in millisecondi) calcolati su griglie di dimensioni crescenti da 10x10 a 100x100._
