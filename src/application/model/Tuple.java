package application.model;

public class Tuple <T1,T2>{
	
	private T1 fst;
	private T2 snd;
	
	public Tuple(T1 fst, T2 snd){
		this.fst = fst;
		this.snd = snd;
	}
	
	public T1 fst() {
		return this.fst;
	}
	
	public T2 snd() {
		return this.snd;
	}
	
	public void setFst(T1 fst) {
		this.fst = fst;
	}
	
	public void setSnd(T2 snd) {
		this.snd = snd;
	}
}
