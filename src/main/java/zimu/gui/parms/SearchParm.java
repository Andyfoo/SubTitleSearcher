package zimu.gui.parms;

public class SearchParm {
	public static SearchParm def = new SearchParm();
	
	
	public boolean from_sheshou = true;
	public boolean from_xunlei = true;
	public boolean from_zimuku = true;
	public boolean from_subhd = false;
	
//	public boolean from_sheshou = false;
//	public boolean from_xunlei = false;
//	public boolean from_zimuku = false;
//	public boolean from_subhd = true;
	


	public boolean isFrom_sheshou() {
		return from_sheshou;
	}



	public void setFrom_sheshou(boolean from_sheshou) {
		this.from_sheshou = from_sheshou;
	}



	public boolean isFrom_xunlei() {
		return from_xunlei;
	}



	public void setFrom_xunlei(boolean from_xunlei) {
		this.from_xunlei = from_xunlei;
	}



	public boolean isFrom_zimuku() {
		return from_zimuku;
	}



	public void setFrom_zimuku(boolean from_zimuku) {
		this.from_zimuku = from_zimuku;
	}



	public boolean isFrom_subhd() {
		return from_subhd;
	}



	public void setFrom_subhd(boolean from_subhd) {
		this.from_subhd = from_subhd;
	}



	
}
