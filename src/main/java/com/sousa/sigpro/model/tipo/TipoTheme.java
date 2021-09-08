package com.sousa.sigpro.model.tipo;

public enum TipoTheme {
	
	start("start"),
	bluesky("bluesky"),
	bootstrap("bootstrap"),
	sam("sam"),
	afterdark("afterdark"),
	afternoon("afternoon"),
	blitzer("blitzer"),
	casablanca("casablanca"),
	cruze("cruze"),
	cupertino("cupertino"),
	delta("delta"),
	eggplant("eggplant"),
	flick("flick"),
	home("home"),
	humanity("humanity"),
	midnight("midnight"),
	overcast("overcast"),
	redmond("redmond"),
	rocket("rocket"),
	smoothness("smoothness"),
	trontastic("trontastic"),
	vader("vader"),
	sunny("sunny"),
	
	uilightness("ui-lightness"),
	southstreet("south-street"),
	mintchoc("mint-choc"),
	dotluv("dot-luv"),
	excitebike("excite-bike"),
	peppergrinder("pepper-grinder"),
	darkhive("dark-hive");
	
	
	private String descricao;
	
	private TipoTheme(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
}