package application.model;

public class Tuple <T,T2> {
	private T fst;
	private T2 snd;
	
	public Tuple(T fst, T2 snd) {
		this.fst = fst;
		this.snd = snd;
	}
	
	public T fst() {
		return this.fst;
	}
	
	public T2 snd() {
		return this.snd;
	}
	
	public void setFst(T fst) {
		this.fst = fst;
	}
	
	public void setSnd(T2 snd) {
		this.snd = snd;
	}
}
