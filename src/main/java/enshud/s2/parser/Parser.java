package enshud.s2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Parser().run("data/ts/normal01.ts");
		//new Parser().run("data/ts/normal02.ts");

		// synerrの確認
		//new Parser().run("data/ts/synerr01.ts");
		//new Parser().run("data/ts/synerr02.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるParser実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，構文解析を行う．
	 * 構文が正しい場合は標準出力に"OK"を，正しくない場合は"Syntax error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること．
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力tsファイル名
	 */
	public static class Pascal {
		static List<String> TS = new ArrayList<String>();

		void set_TS(final String data) {
			TS.add(data);
		}
		String get_TS(final int No) {
			return TS.get(No);
		}
	}
	public void run(final String inputFileName) {

		// TODO
		try {
			File file = new File(inputFileName);

			if(file.exists()) {
				FileReader filereader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String data;
				Pascal pascal = new Pascal();
				while((data = bufferedReader.readLine()) != null) {
					pascal.set_TS(data);
				}

				program(0);





				boolean correct=true;
				List<Integer> ID = new ArrayList<Integer>();
				Integer[] id = {17,43,37,-3,-23,42};
				ID.addAll(0,Arrays.asList(id));

			}


			System.out.print("OK\n");
		}catch(IOException e){
			e.printStackTrace();
		}

	}

	private boolean token_to_ID(final int token,final List<Integer> ID) {

		Integer[] id = {};
		switch(ID.remove(0)) {
			case -3:{
				ID.add(0,-15);
				if(token == 21) ID.addAll(0,Arrays.asList(-5,21));
				break;
			}case -4:{
				if(token == 21) ID.addAll(0,Arrays.asList(-5,21));
				break;
			}case -5:{
				if(token == 43) {
					ID.addAll(0,Arrays.asList(43,-6,38,-8,37,-5));
				}else {
					return false;
				}
				break;

			}case -6:{
				if(token == 41) ID.addAll(0,Arrays.asList(41,43,-6));
				break;

			}case -8:{
				ID.add(0,-9);
					if(token == 1) {
						ID.addAll(0,Arrays.asList(1,35,-13,39,-13,36,14));
					}else if(token!=3 && token!=4 && token!=11) {
						return false;
					}
				break;
			}case -13:{
				ID.add(0,44);
				if(token==30 || token==31) {
					ID.add(0,token);
				}
				break;
			}case -15:{
				if(token == 16) ID.addAll(0,Arrays.asList(-17,-4,23,-15));
				break;
			}case -17:{
				ID.addAll(0,Arrays.asList(16,43,-19));
				break;
			}case -19:{
				if(token == 33) ID.addAll(0,Arrays.asList(33,43,-21,38,-9,-20,34));
				break;
			}case -20:{
				if(token == 37) ID.addAll(0,Arrays.asList(37,43,-21,38,-9,-20));
				break;
			}case -21:{
				if(token == 41) ID.addAll(0,Arrays.asList(41,43,-21));
				break;
			}case -23:{
				ID.addAll(0,Arrays.asList(2,-24,8));
				break;
			}case -24:{
				if(token == -25) ID.addAll(0,Arrays.asList(-25,37,-24));
				break;
			}case -25:{
				if(token == 43) {
					ID.addAll(0,Arrays.asList(-26));
				}else if(token == 10) {

				}else if(token == 22) {
					ID.addAll(0,Arrays.asList(22,-35,6,-23));
				}
				break;
			}case -7:{	//else

			}


		}


		return true;

	}

	private void program(final int No) {

		Pascal pascal = new Pascal();
		//System.out.print("here");
		String x;
		for(int i=No;i<Pascal.TS.size();i++) {

			System.out.print(Pascal.TS.get(i) + "\n");
		}

	}

}
