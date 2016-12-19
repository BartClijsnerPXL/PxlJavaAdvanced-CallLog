package be.pxl.calllog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CallLog implements Comparable<CallLog> {
	
	private Integer id;				// 0
	private String naam;			// 1
	private Date datum;				// 2 + 3
	private String bedrijf;			// 4
	private String omschrijving;	// 5
	private int prio;				// 6
	private CallLogStatus status;	// 7
	
	public CallLog(Integer id, String naam, Date datum, String bedrijf, String omschrijving, int prio, CallLogStatus status) {
		this.id = id;
		this.naam = naam;
		this.datum = datum;
		this.bedrijf = bedrijf;
		this.omschrijving = omschrijving;
		this.prio = prio;
		this.status = status;
	}

	public CallLog() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	/**
	 * Get the datum formatted by CallLogFactory.DATEFORMAT
	 * @return String
	 */
	public String getDatumFormatted() {
		return CallLogFactory.DATEFORMAT.format(datum);
	}
	
	/**
	 * Get the java 8 LocalDate representation of datum
	 * @return LocalDate
	 */
	public LocalDate getLocalDate() {
		return getDatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();	
	}

	public String getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(String bedrijf) {
		this.bedrijf = bedrijf;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}

	public CallLogStatus getStatus() {
		return status;
	}

	public void setStatus(CallLogStatus status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "CallLog [id=" + id + ", naam=" + naam + ", datum=" + getDatumFormatted() + ", bedrijf=" + bedrijf + ", omschrijving="
				+ omschrijving + ", prio=" + prio + ", status=" + status + "]";
	}
	
	
	/**
	 * Compare op bedrijf
	 * Let op compares die gelijk uitkomen en het gebruik van Sets 
	 */
	@Override
	public int compareTo(CallLog callLog) {
		int bedrijfComparison = this.getBedrijf().compareTo(callLog.getBedrijf());
		if(bedrijfComparison == 0) {
			return this.getId().compareTo(callLog.getId());
		}
		return bedrijfComparison;
	}

}
