package enshud.s3.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Checker {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Checker().run("data/ts/normal07.ts");
		//new Checker().run("data/ts/normal02.ts");

		// synerrの確認
		//new Checker().run("data/ts/synerr08.ts");
		//new Checker().run("data/ts/synerr02.ts");

		// semerrの確認
		//new Checker().run("data/ts/semerr04.ts");
		//new Checker().run("data/ts/semerr07.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるChecker実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，意味解析を行う．
	 * 意味的に正しい場合は標準出力に"OK"を，正しくない場合は"Semantic error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Semantic error: line 6"）．
	 * また，構文的なエラーが含まれる場合もエラーメッセージを表示すること（例： "Syntax error: line 1"）．
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

		int ID() {
			String[] x = TS.get(No).split("\\t",4);
			return Integer.parseInt(x[2]);
		}

		String name() {
			String[] x = TS.get(No).split("\\t",4);
			return x[0];
		}

		void clear() {
			TS.clear();
			No=0;
		}

	}


	static class Variable {
		static HashMap<String,HashMap<String,List<String>>> pro_map = new HashMap<String,HashMap<String,List<String>>>();

		void clear() {
			pro_map.clear();
		}

		void set_var(final String var,final String program, final List<String> type) {
			pro_map.get(program).put(var, type);
		}

		void add_pro(final String program) {
			HashMap<String,List<String>> x = new HashMap<String,List<String>>();
			pro_map.put(program,x);
		}

		Boolean search_var(final String var,final String program) {
			if(pro_map.get(program).containsKey(var)) return true;
			return false;
		}

		Boolean search_var_all(final String var,final String program) {
			if(pro_map.get(program).containsKey(var) || pro_map.get("program").containsKey(var)) return true;
			return false;
		}

		Boolean search_proc(final String pro) {
			if(pro_map.containsKey(pro)) return true;
			else return false;
		}

		String get_type(final String program,final String var) {
			if(pro_map.get(program).containsKey(var))	return pro_map.get(program).get(var).get(0);
			else if(pro_map.get("program").containsKey(var))	return pro_map.get("program").get(var).get(0);
			else return "null";
		}

		String get_array(final String program,final String var) {
			if(pro_map.get(program).containsKey(var))	return pro_map.get(program).get(var).get(1);
			else if(pro_map.get("program").containsKey(var))	return pro_map.get("program").get(var).get(1);
			else return "null";
		}
	}

	class CodeError extends Exception{

		public CodeError(String string) {
			Data data = new Data();
			if(string.equals("Syntax")) {
				System.err.println("Syntax error: line "+data.line());
			}else if(string.equals("Semantic")) {
				System.err.println("Semantic error: line "+data.line());
			}
		}

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

				Variable Var = new Variable();
				Var.clear();
				program();
				bufferedReader.close();
				System.out.println("OK");
			}else {
				System.err.print("File not found");
			}

		}catch(IOException e){
			e.printStackTrace();
		}catch(CodeError e) {

		}
	}

	private void program() throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		String program = "program";
		Var.add_pro(program);
		if(data.ID() != 17) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 43) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 37) throw new CodeError("Syntax");
		data.inc();
		block();
		begin(program);
		if(data.ID() != 42) throw new CodeError("Syntax");

	}

	private void block() throws CodeError {
		Data data = new Data();

		if(data.ID() == 21) {
			data.inc();
			vardec_array("program");
		}
		sub_programs();
	}

	private void vardec_array(final String program) throws CodeError {
		Data data = new Data();
		List<String> name = new ArrayList<String>();

		do {
			var(name,program);
			if(data.ID() != 38) throw new CodeError("Syntax");
			data.inc();
			type(name,program);
			if(data.ID() != 37) throw new CodeError("Syntax");
			data.inc();
			name.clear();
		}while(data.ID() == 43);

	}

	private void var(final List<String> name,final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		while(true) {
			if(data.ID() != 43) throw new CodeError("Syntax");
			if(Var.search_var(data.name(),program) || Var.search_proc(data.name()) || name.contains(data.name())) throw new CodeError("Semantic");
			name.add(data.name());
			data.inc();
			if(data.ID() != 41) break;
			data.inc();
		}
	}

	private void type(final List<String> name,final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		if(data.ID()==3 || data.ID()==4 || data.ID()==11) {
			List<String> type = new ArrayList<String>();
			type.add(data.name());
			for(String x:name) Var.set_var(x,program,type);
			data.inc();
		}else {
			array_type(name,program);
		}
	}

	private void array_type(final List<String> name,final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		String min,max;

		if(data.ID() != 1) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 35) throw new CodeError("Syntax");
		data.inc();
		min = number();
		if(data.ID() != 39) throw new CodeError("Syntax");
		data.inc();
		max = number();
		if(data.ID() != 36) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 14) throw new CodeError("Syntax");
		data.inc();
		if(data.ID()!=3 && data.ID()!=4 && data.ID()!=11) throw new CodeError("Syntax");
		if(Integer.parseInt(max) <= Integer.parseInt(min)) throw new CodeError("Semantic");
		List<String> type = new ArrayList<String>();
		type.add("array");
		type.add(data.name());
		type.add(min);
		type.add(max);
		for(String x:name) Var.set_var(x,program,type);
		data.inc();
	}

	private String number() throws CodeError {
		Data data = new Data();

		if(data.ID()==30 || data.ID()==31) data.inc();
		if(data.ID() != 44) throw new CodeError("Syntax");
		String x = data.name();
		data.inc();
		return x;
	}

	private void sub_programs() throws CodeError {
		Data data = new Data();
		String program;

		while(data.ID() == 16) {
			program = sub_head();
			if(data.ID()==21) {
				data.inc();
				vardec_array(program);
			}
			begin(program);
			if(data.ID() != 37) throw new CodeError("Syntax");
			data.inc();
		}

	}

	private String sub_head() throws CodeError{
		Data data = new Data();
		Variable Var = new Variable();
		List<String> name = new ArrayList<String>();
		String program;

		if(data.ID() != 16) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 43) throw new CodeError("Syntax");
		program = data.name();
		if(Var.search_proc(program)) throw new CodeError("Semantic");
		Var.add_pro(data.name());
		data.inc();
		parameter(program,name);
		if(data.ID() != 37) throw new CodeError("Syntax");
		data.inc();

		return program;
	}

	private void parameter(final String program,final List<String> name) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		if(data.ID() == 33) {
			do{
				data.inc();
				para_names(name,program);
				if(data.ID() != 38) throw new CodeError("Syntax");
				data.inc();
				if(data.ID()!=3 && data.ID()!=4 && data.ID()!=11) throw new CodeError("Syntax");
				List<String> type = new ArrayList<String>();
				type.add(data.name());
				for(String x:name) Var.set_var(x,program,type);
				data.inc();

			}while(data.ID() == 37);

			if(data.ID() != 34) throw new CodeError("Syntax");
			data.inc();
		}
	}

	private void para_names(final List<String> name,final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		while(true) {
			if(data.ID() != 43) throw new CodeError("Syntax");
			if(Var.search_var(data.name(),program) || Var.search_proc(data.name()) || name.contains(data.name())) throw new CodeError("Semantic");
			name.add(data.name());
			data.inc();
			if(data.ID() != 41) break;
			data.inc();
		}
	}

	private void begin(final String program) throws CodeError {
		Data data = new Data();

		if(data.ID() != 2) throw new CodeError("Syntax");
		data.inc();
		sentence(program);
		if(data.ID() != 8) throw new CodeError("Syntax");
		data.inc();
	}

	private void sentence(final String program) throws CodeError {
		Data data = new Data();

		if(data.ID()!=43 && data.ID()!=23 &&data.ID()!=18 && data.ID()!=2 && data.ID()!=10 && data.ID()!=22) throw new CodeError("Syntax");
		do {
			if(data.ID()==43 || data.ID()==23 || data.ID()==18 || data.ID()==2) sent_default(program);
			else if(data.ID() == 10) sent_if(program);
			else if(data.ID() == 22) sent_while(program);
			else break;

			if(data.ID() != 37) throw new CodeError("Syntax");
			data.inc();
		}while(true);
	}

	private void sent_default(final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		if(data.ID() == 2) begin(program);
		else if(data.ID() == 18 || data.ID() == 23) in_out(program);
		else {
			if(data.ID() != 43) throw new CodeError("Syntax");
			String name = data.name();
			data.inc();

			if(Var.search_var_all(name, program)) assign(program,name);
			else if(Var.search_proc(name)) procedure(program,name);
			else throw new CodeError("Semantic");

		}

	}

	private void assign(final String program,final String var) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		String type = Var.get_type(program, var);

		if(!Var.search_var_all(var, program)) throw new CodeError("Semantic");
		if(data.ID() == 35) {
			data.inc();
			if(!formula(program).equals("integer")) throw new CodeError("Semantic");
			if(data.ID() != 36) throw new CodeError("Syntax");
			data.inc();
			type = Var.get_array(program, var);
		}

		if(data.ID() != 40) throw new CodeError("Syntax");
		data.inc();
		if(!type.equals(formula(program))) throw new CodeError("Semantic");
	}

	private String formula(final String program) throws CodeError {
		Data data = new Data();
		String type;

		if(data.ID()==30 || data.ID()==31) {
			data.inc();
			type = "integer";
			if(!term(program).equals(type)) throw new CodeError("Semantic");
		}else {
			type = term(program);
		}

		while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
			data.inc();
			if(!type.equals(term(program))) throw new CodeError("Semantic");
		}

		if(data.ID()==24 || data.ID()==25 || data.ID()==26 || data.ID()==27 || data.ID()==28 || data.ID()==29) {
			data.inc();
			if(data.ID()==30 || data.ID()==31) {
				if(type.equals("integer"))data.inc();
				else throw new CodeError("Semantic");
			}
			if(!type.equals(term(program))) throw new CodeError("Semantic");
			while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
				data.inc();
				if(!type.equals(term(program))) throw new CodeError("Semantic");
			}
			type = "boolean";
		}
		return type;

	}

	private String term(final String program) throws CodeError {
		Data data = new Data();
		String type;

		type = factor(program);
		while(data.ID()==32 || data.ID()==5 || data.ID()==0 || data.ID()==12) {
			data.inc();
			if (!type.equals(factor(program))) throw new CodeError("Semantic");
		}
		return type;

	}

	private String factor(final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		String type;
		type = data.name();

		if(data.ID() == 13) {
			data.inc();
			type = factor(program);
			if(!type.equals("boolean")) throw new CodeError("Semantic");
		}else if(data.ID() == 33) {
			data.inc();
			type = formula(program);
			if(data.ID() != 34) throw new CodeError("Syntax");
			data.inc();
		}else if(data.ID()==44) {
			data.inc();
			type = "integer";
		}else if(data.ID()==45) {
			data.inc();
			type = "char";
		}else if(data.ID()==9 || data.ID()==20) {
			data.inc();
			type = "boolean";
		}else if(data.ID() ==43) {
			type = Var.get_type(program,data.name());
			String name = data.name();
			data.inc();
			if(data.ID() == 35) {
				type = Var.get_array(program,name);
				String No;
				data.inc();
				No = formula(program);
				if(data.ID() != 36) throw new CodeError("Syntax");
				if(!No.equals("integer")) throw new CodeError("Sematic");
				data.inc();
			}
		}else throw new CodeError("Syntax");
		return type;

	}

	private void procedure(final String program,final String pro_name) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		if(data.ID() == 33) {
			data.inc();
			formula_array(program);
			if(data.ID() != 34)throw new CodeError("Syntax");
			data.inc();
		}
		if(!Var.search_proc(pro_name)) throw new CodeError("Semantic");
	}

	private void formula_array(final String program) throws CodeError {
		Data data = new Data();

		formula(program);
		while(data.ID() == 41) {
			data.inc();
			formula(program);
		}
	}

	private void in_out(final String program) throws CodeError {
		Data data = new Data();

		if(data.ID() == 18) {
			data.inc();
			if(data.ID() == 33) {
				data.inc();
				var_array(program);
				if(data.ID() != 34) throw new CodeError("Syntax");
				data.inc();
			}
		}else if(data.ID() == 23) {
			data.inc();
			if(data.ID() == 33) {
				data.inc();
				formula_array(program);
				if(data.ID() != 34) throw new CodeError("Syntax");
				data.inc();
			}
		}else throw new CodeError("Syntax");
	}

	private void var_array(final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		String var;

		if(data.ID() != 43) throw new CodeError("Syntax");
		var = data.name();
		if(!Var.search_var_all(var,program)) throw new CodeError("Semantic");
		data.inc();
		if(data.ID() == 35) {
			data.inc();
			formula(program);
			if(data.ID() != 36) throw new CodeError("Syntax");
			data.inc();
		}

		while(data.ID() == 41) {
			data.inc();
			if(data.ID() != 43) throw new CodeError("Syntax");
			data.inc();
			if(data.ID() == 35) {
				data.inc();
				formula(program);
				if(data.ID() != 36) throw new CodeError("Syntax");
				data.inc();
			}
		}
	}

	private void sent_if(final String program) throws CodeError {
		Data data = new Data();

		if(data.ID() != 10) throw new CodeError("Syntax");
		data.inc();
		if(!formula(program).equals("boolean")) throw new CodeError("Semantic");
		if(data.ID() != 19) throw new CodeError("Syntax");
		data.inc();
		begin(program);
		if(data.ID() == 7) {
			data.inc();
			begin(program);
		}
	}

	private void sent_while(final String program) throws CodeError {
		Data data = new Data();

		if(data.ID() != 22) throw new CodeError("Syntax");
		data.inc();
		if(!formula(program).equals("boolean")) throw new CodeError("Semantic");
		if(data.ID() != 6) throw new CodeError("Syntax");
		data.inc();
		begin(program);
	}

}
