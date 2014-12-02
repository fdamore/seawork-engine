package org.uario.seaworkengine.utility;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class CFGenerator {
	private final int			anno, giorno;
	private final int[]			elencoDispari	= { 1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 2, 4, 18, 20, 11, 3, 6, 8,
			12, 14, 16, 10, 22, 25, 24, 23		};

	// Array statici
	private final char[]		elencoPari		= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private final String[][]	mese			= { { "Gennaio", "A" }, { "Febbraio", "B" }, { "Marzo", "C" }, { "Aprile", "D" }, { "Maggio", "E" },
			{ "Giugno", "H" }, { "Luglio", "L" }, { "Agosto", "M" }, { "Settembre", "P" }, { "Ottobre", "R" }, { "Novembre", "S" },
			{ "Dicembre", "T" }				};
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Variabili di istanza
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------
	private final String		nome, cognome, comune, m, sesso;

	// Inizializza le variabili di istanza della classe
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------
	public CFGenerator(final String nome, final String cognome, final String comune, final String m, final int anno, final int giorno,
			final String sesso) {
		this.nome = nome;
		this.cognome = cognome;
		this.comune = comune;
		this.m = m;
		this.anno = anno;
		this.giorno = giorno;
		this.sesso = sesso;

	} // Fine costruttore
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Aggiunge le vocali alla stringa passata per parametro
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String aggiungiVocali(String stringa, final String vocali) {
		int index = 0;
		while (stringa.length() < 3) {
			stringa += vocali.charAt(index);
			index++;
		}
		return stringa;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Aggiunge le X sino a raggiungere una lunghezza complessiva di 3 caratteri
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String aggiungiX(String stringa) {
		while (stringa.length() < 3) {
			stringa += "x";
		}
		return stringa;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Calcolo del Codice di Controllo
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String calcolaCodice() {
		final String str = this.getCognome().toUpperCase() + this.getNome().toUpperCase() + this.getAnno() + this.getMese() + this.getGiorno()
				+ this.getComune();
		int pari = 0, dispari = 0;

		for (int i = 0; i < str.length(); i++) {
			final char ch = str.charAt(i); // i-esimo carattere della stringa

			// Il primo carattere e' il numero 1 non 0
			if ((i + 1) % 2 == 0) {
				final int index = Arrays.binarySearch(this.elencoPari, ch);
				pari += (index >= 10) ? index - 10 : index;
			} else {
				final int index = Arrays.binarySearch(this.elencoPari, ch);
				dispari += this.elencoDispari[index];
			}
		}

		int controllo = (pari + dispari) % 26;
		controllo += 10;

		return this.elencoPari[controllo] + "";
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Elabora codice del comune
	// ------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String elaboraCodiceComune() {
		String cc = "";
		try {
			final Scanner scanner = new Scanner(new File("Comuni.txt"));
			scanner.useDelimiter("\r\n");

			while (scanner.hasNext()) {
				final String s1 = scanner.nextLine();
				final String s2 = s1.substring(0, s1.indexOf('-') - 1);
				System.out.println(s2);
				if (s2.equalsIgnoreCase(this.comune)) {
					cc = s1.substring(s1.lastIndexOf(' ') + 1);
				}
			}

			scanner.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return cc;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	int getAnno() {
		return (this.anno % 100);
	}

	int getAnnoInserito() {
		return this.anno;
	}

	String getCodice() {
		return this.calcolaCodice();
	}

	public String getCodiceFiscale() {
		return this.toString();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------------------
	String getCognome() {
		return this.modificaNC(this.cognome, false);
	}

	String getCognomeInserito() {
		return this.cognome;
	}

	String getComune() {
		return this.elaboraCodiceComune();
	}

	// Toglie dalla stringa tutte le vocali
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String getConsonanti(String stringa) {
		stringa = stringa.replaceAll("[aeiou]", "");
		return stringa;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	int getGiorno() {
		return (this.sesso.equals("M")) ? this.giorno : (this.giorno + 40);
	}

	// I seguenti metodi svolgono le operazioni specifiche sui dati

	int getGiornoInserito() {
		return this.giorno;
	}

	String getMese() {
		return this.modificaMese();
	}

	String getMeseInserito() {
		return this.m;
	}

	// Metogi getter per ottenere gli elementi della classe
	// Interfacce pi� comode ed ordinate per l'accesso alle funzionalit�
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	String getNome() {
		return this.modificaNC(this.nome, true);
	}

	String getNomeInserito() {
		return this.nome;
	}

	// Toglie dalla stringa tutte le consonanti
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String getVocali(String stringa) {
		stringa = stringa.replaceAll("[^aeiou]", "");
		return stringa;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Restituisce il codice del mese
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String modificaMese() {
		for (int i = 0; i < this.mese.length; i++) {
			if (this.mese[i][0].equalsIgnoreCase(this.m)) {
				return this.mese[i][1];
			}
		}
		return null;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * @param stringa
	 *            Corrisponde al nome/cognome da modificare
	 * @param cod
	 *            Se cod e' true, indica il nome; altrimenti il cognome
	 * @return nuovaStringa Restituisce la stringa modificata
	 */
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private String modificaNC(String stringa, final boolean cod) {
		String nuovastringa = "";
		stringa = stringa.replaceAll(" ", ""); // Rimuovo eventuali spazi
		stringa = stringa.toLowerCase();

		final String consonanti = this.getConsonanti(stringa); // Ottengo tutte
		// le consonanti
		// e tutte le
		// vocali della
		// stringa
		final String vocali = this.getVocali(stringa);

		// Controlla i possibili casi
		if (consonanti.length() == 3) { // La stringa contiene solo 3
			// consonanti, quindi ho gia' la
			// modifica
			nuovastringa = consonanti;
		}
		// Le consonanti non sono sufficienti, e la stinga e' pi� lunga o
		// uguale a 3 caratteri [aggiungo le vocali mancanti]
		else if ((consonanti.length() < 3) && (stringa.length() >= 3)) {
			nuovastringa = consonanti;
			nuovastringa = this.aggiungiVocali(nuovastringa, vocali);
		}
		// Le consonanti non sono sufficienti, e la stringa
		// contiene meno di 3 caratteri [aggiungo consonanti e vocali, e le x]
		else if ((consonanti.length() < 3) && (stringa.length() < 3)) {
			nuovastringa = consonanti;
			nuovastringa += vocali;
			nuovastringa = this.aggiungiX(nuovastringa);
		}
		// Le consonanti sono in eccesso, prendo solo le
		// prime 3 nel caso del cognome; nel caso del nome la 0, 2, 3
		else if (consonanti.length() > 3) {
			// true indica il nome e false il cognome
			if (!cod) {
				nuovastringa = consonanti.substring(0, 3);
			} else {
				nuovastringa = consonanti.charAt(0) + "" + consonanti.charAt(2) + "" + consonanti.charAt(3);
			}
		}

		return nuovastringa;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Viene richiamato per una stampa
	@Override
	public String toString() {
		return this.getCognome().toUpperCase() + this.getNome().toUpperCase() + this.getAnno() + this.getMese() + this.getGiorno() + this.getComune()
				+ this.getCodice();
	}

}