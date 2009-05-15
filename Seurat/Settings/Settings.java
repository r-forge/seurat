package Settings;

public class Settings {
	public int PixelSize = 2;

	public int Model = 1;

	public double colorParam = 0.5;
	
	public double aPos = 0.5,bPos = 1;
	public double aNeg = 0.5,bNeg = 1;
	
	public double posMin = 0, posMax = 1;
	public double negMax = 1, negMin = 0;
	
	public double MAXP,MINP;
	
	public boolean invertShading = false;

	public int getPixelSize() {
		return PixelSize;
	}

	public void setPixelSize(int pixelSize) {
		PixelSize = pixelSize;
	}

	public int getModel() {
		return Model;
	}

	public void setModel(int model) {
		Model = model;
	}

	public double getColorParam() {
		return colorParam;
	}

	public void setColorParam(double colorParam) {
		this.colorParam = colorParam;
	}

	public double getAPos() {
		return aPos;
	}

	public void setAPos(double pos) {
		aPos = pos;
	}

	public double getBPos() {
		return bPos;
	}

	public void setBPos(double pos) {
		bPos = pos;
	}

	public double getANeg() {
		return aNeg;
	}

	public void setANeg(double neg) {
		aNeg = neg;
	}

	public double getBNeg() {
		return bNeg;
	}

	public void setBNeg(double neg) {
		bNeg = neg;
	}

	public double getPosMin() {
		return posMin;
	}

	public void setPosMin(double posMin) {
		this.posMin = posMin;
	}

	public double getPosMax() {
		return posMax;
	}

	public void setPosMax(double posMax) {
		this.posMax = posMax;
	}

	public double getNegMax() {
		return negMax;
	}

	public void setNegMax(double negMax) {
		this.negMax = negMax;
	}

	public double getNegMin() {
		return negMin;
	}

	public void setNegMin(double negMin) {
		this.negMin = negMin;
	}

	public double getMAXP() {
		return MAXP;
	}

	public void setMAXP(double maxp) {
		MAXP = maxp;
	}

	public double getMINP() {
		return MINP;
	}

	public void setMINP(double minp) {
		MINP = minp;
	}

	public boolean isInvertShading() {
		return invertShading;
	}

	public void setInvertShading(boolean invertShading) {
		this.invertShading = invertShading;
	}
	
	
}
