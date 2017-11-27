package application.model;

public class Tuple <T> {
	private T fst;
	private T snd;
	
	public Tuple(T fst, T snd) {
		this.fst = fst;
		this.snd = snd;
	}
	
	public T fst() {
		return this.fst;
	}
	
	public T snd() {
		return this.snd;
	}
	
	public void setFst(T fst) {
		this.fst = fst;
	}
	
	public void setSnd(T snd) {
		this.snd = snd;
	}
}
