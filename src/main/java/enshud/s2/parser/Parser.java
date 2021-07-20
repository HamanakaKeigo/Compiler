package enshud.s2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		//new Parser().run("data/ts/normal01.ts");
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
	static class Data {
		static List<String> TS = new ArrayList<String>();
		static int No=0;

		void set_TS(final String data) {
			TS.add(data);
		}

		void inc() {
			No++;
		}

		int line() {
			String[] x = TS.get(No).split("\\t",4);
			return Integer.parseInt(x[3]);
		}

		void clear() {
			TS.clear();
			No=0;
		}

		int ID() {
			String[] x = TS.get(No).split("\\t",4);
			return Integer.parseInt(x[2]);
		}
	}


	class PascalError extends Exception{

	}

	public void run(final String inputFileName) {

		// TODO

		Data data = new Data();
		try {
			File file = new File(inputFileName);
			if(file.exists()) {

				FileReader filereader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String text;

				data.clear();
				while((text = bufferedReader.readLine()) != null) {
					data.set_TS(text);
				}

				program();
				System.out.print("OK");
				bufferedReader.close();
			}else {
				System.err.print("File not found");
			}

		}catch(IOException e){
			e.printStackTrace();
		}catch(PascalError e) {
			System.err.print("Syntax error: line "+data.line());
		}

	}


	private void program() throws PascalError {
		Data data = new Data();

		if(data.ID() != 17) throw new PascalError();
		data.inc();
		if(data.ID() != 43) throw new PascalError();
		data.inc();
		if(data.ID() != 37) throw new PascalError();
		data.inc();
		block();
		begin();
		if(data.ID() != 42) throw new PascalError();

	}

	private void block() throws PascalError {
		Data data = new Data();

		if(data.ID() == 21) {
			data.inc();
			vardec_array();
		}

		sub_programs();

	}

	private void vardec_array() throws PascalError {
		Data data = new Data();

		do {
			var();
			if(data.ID() != 38) throw new PascalError();
			data.inc();
			type();
			if(data.ID() != 37) throw new PascalError();
			data.inc();
		}while(data.ID() == 43);

	}

	private void var() throws PascalError {
		Data data = new Data();

		if(data.ID() != 43) throw new PascalError();
		data.inc();

		while(data.ID() == 41) {
			data.inc();
			if(data.ID() != 43) throw new PascalError();
			data.inc();
		}
	}

	private void type() throws PascalError {
		Data data = new Data();
		if(data.ID()==3 || data.ID()==4 || data.ID()==11) {
			data.inc();
		}else {
			array_type();
		}
	}

	private void array_type() throws PascalError {
		Data data = new Data();

		if(data.ID() != 1) throw new PascalError();
		data.inc();
		if(data.ID() != 35) throw new PascalError();
		data.inc();
		number();
		if(data.ID() != 39) throw new PascalError();
		data.inc();
		number();
		if(data.ID() != 36) throw new PascalError();
		data.inc();
		if(data.ID() != 14) throw new PascalError();
		data.inc();
		if(data.ID()!=3 && data.ID()!=4 && data.ID()!=11) throw new PascalError();
		data.inc();

	}

	private void number() throws PascalError {
		Data data = new Data();

		if(data.ID()==30 || data.ID()==31) data.inc();
		if(data.ID() != 44) throw new PascalError();
		data.inc();

	}

	private void sub_programs() throws PascalError {
		Data data = new Data();

		while(data.ID() == 16) {
			sub_head();
			if(data.ID()==21) {
				data.inc();
				vardec_array();
			}
			begin();
			if(data.ID() != 37) throw new PascalError();
			data.inc();
		}


	}

	private void sub_head() throws PascalError{
		Data data = new Data();

		if(data.ID() != 16) throw new PascalError();
		data.inc();
		if(data.ID() != 43) throw new PascalError();
		data.inc();
		parameter();
		if(data.ID() != 37) throw new PascalError();
		data.inc();

	}

	private void parameter() throws PascalError {
		Data data = new Data();

		if(data.ID() == 33) {
			do{
				data.inc();
				para_names();
				if(data.ID() != 38) throw new PascalError();
				data.inc();
				if(data.ID()!=3 && data.ID()!=4 && data.ID()!=11) throw new PascalError();
				data.inc();

			}while(data.ID() == 37);

			if(data.ID() != 34) throw new PascalError();
			data.inc();

		}
	}

	private void para_names() throws PascalError {
		Data data = new Data();

		if(data.ID() != 43) throw new PascalError();
		data.inc();

		while(data.ID() == 41) {
			data.inc();
			if(data.ID() != 43) throw new PascalError();
			data.inc();
		}
	}

	private void begin() throws PascalError {
		Data data = new Data();

		if(data.ID() != 2) throw new PascalError();
		data.inc();
		sentence();
		if(data.ID() != 8) throw new PascalError();
		data.inc();
	}

	private void sentence() throws PascalError {

		Data data = new Data();
		if(data.ID()!=43 && data.ID()!=23 &&data.ID()!=18 && data.ID()!=2 && data.ID()!=10 && data.ID()!=22) throw new PascalError();

		do {
			if(data.ID()==43 || data.ID()==23 || data.ID()==18 || data.ID()==2) sent_default();
			else if(data.ID() == 10) sent_if();
			else if(data.ID() == 22) sent_while();
			else break;

			if(data.ID() != 37) throw new PascalError();
			data.inc();
		}while(true);


	}

	private void sent_default() throws PascalError {
		Data data = new Data();

		if(data.ID() == 2) begin();
		else if(data.ID() == 18 || data.ID() == 23) in_out();
		else {
			if(data.ID() != 43) throw new PascalError();
			data.inc();
			if(data.ID()==40 || data.ID()==35) assign();
			else procedure();
		}

	}

	private void assign() throws PascalError {
		Data data = new Data();

		if(data.ID() == 35) {
			data.inc();
			formula();
			if(data.ID() != 36) throw new PascalError();
			data.inc();
		}

		if(data.ID() != 40) throw new PascalError();
		data.inc();
		formula();
	}

	private void formula() throws PascalError {
		Data data = new Data();

		if(data.ID()==30 || data.ID()==31) data.inc();
		term();
		while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
			data.inc();
			term();
		}

		if(data.ID()==24 || data.ID()==25 || data.ID()==26 || data.ID()==27 || data.ID()==28 || data.ID()==29) {
			data.inc();
			if(data.ID()==30 || data.ID()==31) data.inc();
			term();
			while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
				data.inc();
				term();
			}
		}

	}

	private void term() throws PascalError {
		Data data = new Data();

		factor();
		while(data.ID()==32 || data.ID()==5 || data.ID()==0 || data.ID()==12) {
			data.inc();
			factor();
		}

	}

	private void factor() throws PascalError {
		Data data = new Data();

		if(data.ID() == 13) {
			data.inc();
			term();
		}else if(data.ID() == 33) {
			data.inc();
			formula();
			if(data.ID() != 34) throw new PascalError();
			data.inc();
		}
		else if(data.ID()==44 || data.ID()==45 || data.ID()==9 || data.ID()==20) data.inc();
		else if(data.ID() ==43) {
			data.inc();
			if(data.ID() == 35) {
				data.inc();
				formula();
				if(data.ID() != 36) throw new PascalError();
				data.inc();
			}
		}else throw new PascalError();

	}

	private void procedure() throws PascalError {
		Data data = new Data();

		if(data.ID() == 33) {
			data.inc();
			formula_array();
			if(data.ID() != 34)throw new PascalError();
			data.inc();

		}
	}

	private void formula_array() throws PascalError {
		Data data = new Data();

		formula();
		while(data.ID() == 41) {
			data.inc();
			formula();
		}
	}

	private void in_out() throws PascalError {
		Data data = new Data();

		if(data.ID() == 18) {
			data.inc();
			if(data.ID() == 33) {
				data.inc();
				var_array();
				if(data.ID() != 34) throw new PascalError();
				data.inc();
			}
		}else if(data.ID() == 23) {
			data.inc();
			if(data.ID() == 33) {
				data.inc();
				formula_array();
				if(data.ID() != 34) throw new PascalError();
				data.inc();
			}
		}else throw new PascalError();
	}

	private void var_array() throws PascalError {
		Data data = new Data();

		if(data.ID() != 43) throw new PascalError();
		data.inc();
		if(data.ID() == 35) {
			data.inc();
			formula();
			if(data.ID() != 36) throw new PascalError();
			data.inc();
		}

		while(data.ID() == 41) {
			data.inc();

			if(data.ID() != 43) throw new PascalError();
			data.inc();
			if(data.ID() == 35) {
				data.inc();
				formula();
				if(data.ID() != 36) throw new PascalError();
				data.inc();
			}
		}

	}

	private void sent_if() throws PascalError {
		Data data = new Data();

		if(data.ID() != 10) throw new PascalError();
		data.inc();
		formula();
		if(data.ID() != 19) throw new PascalError();
		data.inc();
		begin();
		if(data.ID() == 7) {
			data.inc();
			begin();
		}
	}

	private void sent_while() throws PascalError {
		Data data = new Data();

		if(data.ID() != 22) throw new PascalError();
		data.inc();
		formula();
		if(data.ID() != 6) throw new PascalError();
		data.inc();
		begin();
	}

}
