package enshud.s1.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Lexer {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		//new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
		//new Lexer().run("data/pas/normal03.pas", "tmp/out3.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は標準出力に"OK"を，
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */
	public class Token {

		public String[][] token = {{"and","SAND"},{"array","SARRAY"},{"begin","SBEGIN"},{"boolean","SBOOLEAN"}
		,{"char","SCHAR"},{"/","SDIVD"},{"do","SDO"},{"else","SELSE"},{"end","SEND"}
		,{"false","SFALSE"},{"if","SIF"},{"integer","SINTEGER"},{"mod","SMOD"}
		,{"not","SNOT"},{"of","SOF"},{"or","SOR"},{"procedure","SPROCEDURE"}
		,{"program","SPROGRAM"},{"readln","SREADLN"},{"then","STHEN"},{"true","STRUE"}
		,{"var","SVAR"},{"while","SWHILE"},{"writeln","SWRITELN"},{"=","SEQUAL"}
		,{"<>","SNOTEQUAL"},{"<","SLESS"},{"<=","SLESSEQUAL"},{">=","SGREATEQUAL"}
		,{">","SGREAT"},{"+","SPLUS"},{"-","SMINUS"},{"*","SSTAR"}
		,{"(","SLPAREN"},{")","SRPAREN"},{"[","SLBRACKET"},{"]","SRBRACKET"}
		,{";","SSEMICOLON"},{":","SCOLON"},{"..","SRANGE"},{":=","SASSIGN"}
		,{",","SCOMMA"},{".","SDOT"},{"名前","SIDENTIFIER"},{"符号なし整数","SCONSTANT"},{"文字列(文字定数)","SSTRING"}
		};

		public String code(int ID) {
			return token[ID][0];
		}
		public String name(int ID) {
			return token[ID][1];
		}

	}
	public void run(final String inputFileName, final String outputFileName) {

		// TODO
		try {
			File file = new File(inputFileName);

			if(file.exists()) {
				Token token = new Token();
				FileReader filereader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				FileWriter fw = new FileWriter(outputFileName);

				String data;
				int line=0;
				int ID=-2;
				boolean comment=false;
				boolean text = false;
				String sentence="";

					while((data = bufferedReader.readLine()) != null) {
						line++;

						String a = data.replaceAll("\\s+|\\t+"," ");
						String[] value = a.split(" ",-1);

						for(int k=0;k<value.length;k++) {
							String word=value[k];
							word = word.concat(" ");

							while(word.length()>0) {
								String[] spells = word.split("",-1);

								if(comment) {
									int i=0;
									for(i=0;i<word.length();i++) {
										word = word.substring(1);
										if(spells[i].equals("}")) {
											comment=false;
											break;
										}
									}
									continue;
								}

								if(text) {
									String x;
									if(data.indexOf("'",1+data.indexOf("'")) < 0) {
										x = data.substring(data.indexOf("'"));
										x.concat("'");
										fw.write(x+"\t" + token.name(45)+"\t" + "45\t" + line+"\n");
										word = "";
										value = word.split(" ",-1);
									}else {
										x = data.substring(data.indexOf("'"),1+data.indexOf("'",1+data.indexOf("'")));
										fw.write(x+"\t" + token.name(45)+"\t" + "45\t" + line+"\n");
										data = data.substring(1+data.indexOf("'",1+data.indexOf("'")));
										word = data.replaceAll("\\s+|\\t+"," ");
										value = word.split(" ",-1);
									}

									word ="";
									k=-1;
									text = false;
									continue;
								}


								switch(spells[0]) {
									case "{":{
										comment=true;
										word = word.substring(1);
										ID=-1;
										break;
									}
									case "'":{
										text = true;
										word = word.substring(1);
										ID=-1;
										break;
									}
									case "/":{
										ID=5;
										break;
									}
									case "=":{
										ID=24;
										break;
									}
									case "<":{
										switch(spells[1]) {
											case ">":{
												ID=25;
												break;
											}
											case "=":{
												ID=27;
												break;
											}
											default:{
												ID=26;
												break;
											}
										}
										break;
									}
									case ">":{
										switch(spells[1]) {
											case "=":{
												ID=28;
												break;
											}
											default:{
												ID=29;
												break;
											}
										}
										break;
									}
									case "+":{
										ID=30;
										break;
									}
									case "-":{
										ID=31;
										break;
									}
									case "*":{
										ID=32;
										break;
									}
									case "(":{
										ID=33;
										break;
									}
									case ")":{
										ID=34;
										break;
									}
									case "[":{
										ID=35;
										break;
									}
									case "]":{
										ID=36;
										break;
									}
									case ";":{
										ID=37;
										break;
									}
									case ":":{
										if(spells[1].equals("=")) {
											ID=40;
										}else{
											ID=38;
										}
										break;
									}
									case ".":{
										if(spells[1].equals(".")) {
											ID=39;
										}else{
											ID=42;
										}
										break;
									}
									case ",":{
										ID=41;
										break;
									}
									case " ":{
										ID=-1;
										word = word.substring(1);
										break;
									}
									default: {
										sentence = sentence.concat(spells[0]);
										word = word.substring(1);
									}
								}


								if(ID>=-1) {
									if(sentence.length()>0) {
										int y=check_id(sentence);
										if(y>=0&&y<=23) {
											fw.write(token.code(y)+"\t" + token.name(y)+"\t" + y+"\t" + line+"\n");
										}else if(y==24) {
											fw.write("div\t" + token.name(5)+"\t" + "5\t" + line+"\n");
										}else {
											if(Character.isDigit(sentence.charAt(0))) {
												int i;
												for(i=0;i<sentence.length();i++) {
													if(!Character.isDigit(sentence.charAt(i))) {
														fw.write(sentence.substring(0,i-1)+"\t" + token.name(44)+"\t" + "44\t" + line+"\n");
														fw.write(sentence.substring(i)+"\t" + token.name(43)+"\t" + "43\t" + line+"\n");
														break;
													}
												}
												if(i>=sentence.length()) {
													fw.write(sentence+"\t" + token.name(44)+"\t" + "44\t" + line+"\n");
												}
											}else {
												fw.write(sentence+"\t" + token.name(43)+"\t" + "43\t" + line+"\n");
											}
										}
										sentence="";
									}

									if(ID<0) {
										ID=-2;
									}else{
										fw.write(token.code(ID)+"\t" + token.name(ID)+"\t" + ID+"\t" + line+"\n");
										word = word.substring(token.code(ID).length());
										ID=-2;
									}
								}

							}

						}
					}

					filereader.close();
				    fw.close();

			}else {
				System.err.print("File not found");
			}

		}catch (IOException e){
			e.printStackTrace();
		}

		System.out.print("OK");
	}

	public int check_id(final String code) {
		Token token = new Token();
		int ID=-1;

		if(code.equals("div")) {
			ID=24;
		}else {
			for(int i=0;i<=23;i++) {
				if(token.code(i).equals(code)) {
					ID=i;
					break;
				}
			}
		}
		return ID;
	}

	public void write_ts(final String outputFileName,final int ID,final int line) {
		Token token = new Token();

		try {
			FileWriter fw = new FileWriter(outputFileName,true);
			fw.write(token.code(ID)+"\t"
					+ token.name(ID)+"\t"
					+ ID + "\t"
					+ line+"\n");
			fw.close();

		}catch (IOException e){
			e.printStackTrace();
		}

	}

}
