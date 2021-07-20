package enshud.s4.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enshud.casl.CaslSimulator;

public class Compiler {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// Compilerを実行してcasを生成する
		new Compiler().run("data/ts/normal09.ts", "tmp/out.cas");

		// 上記casを，CASLアセンブラ & COMETシミュレータで実行する
		CaslSimulator.run("tmp/out.cas", "tmp/out.ans");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
	 * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
	 * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．）
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */

	static private class Casl {

		static private HashMap<String,ArrayList<String>> CaslCode = new HashMap<String,ArrayList<String>>();
		static private ArrayList<String> strings = new ArrayList<String>();
		static private int[] count = {0,0,0,0,0,0};
		//TURE,BOTH,ELSE,LOOP,ENDLP,ENDIF

		void clear() {
			CaslCode.clear();
			Arrays.fill(count,0);
			strings.clear();
		}

		void add_program(final String program) {
			ArrayList<String> x = new ArrayList<String>();
			CaslCode.put(program,x);
		}

		void add_strings(final String string) {
			if(!strings.contains(string)) {
				strings.add(string);
			}
		}
		int get_stringNo(final String string) {
			return strings.indexOf(string);
		}

		void add_code(final String program,final String code) {
			CaslCode.get(program).add(code);
		}

		void output(final String outputFileName) {
			try {
				FileWriter fw = new FileWriter(outputFileName);
				Variable Var = new Variable();
				fw.write("CASL\tSTART\tBEGIN\t;\n");
				fw.write("BEGIN\tLAD\tGR6, 0\t;\n" +
						"\tLAD\tGR7, LIBBUF\t;\n");

				for(String main : CaslCode.get("program")) fw.write(main);
				fw.write("\tRET\t;\n");
				CaslCode.remove("program");

				for( Map.Entry<String,ArrayList<String>> x : CaslCode.entrySet()) {
					fw.write("PROG"+Var.get_proNo(x.getKey())+"\tNOP;\n");
					for(String code : x.getValue()) fw.write(code);
					fw.write("\tRET\t;\n");
				}

				fw.write("LIBBUF\tDS\t256\t;\n");
				for(int i=0;i<strings.size();i++) fw.write("CHAR"+i+"\tDC\t"+strings.get(i)+"\t;\n");

				fw.write("VAR\tDS\t"+Var.get_NOsize()+"\t;\n");
				fw.write("\tEND\t;\n");

				FileReader filereader = new FileReader("data/cas/lib.cas");
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String lib;
				while((lib = bufferedReader.readLine()) != null) fw.write(lib+"\n");

				bufferedReader.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//TURE,BOTH,ELSE,LOOP,ENDLP
		String get_oper() {
			return "OPER"+count[0];
		}
		String get_both() {
			return "BOTH"+count[1];
		}
		String get_else() {
			return "ELSE"+count[2];
		}
		String get_loop() {
			return "LOOP"+count[3];
		}
		String get_endlp() {
			return "ENDLP"+count[4];
		}
		String get_endif() {
			return "ENDIF"+count[5];
		}

		void inc_count(final String name) {
			if(name.equals("OPER"))			count[0]++;
			else if(name.equals("BOTH"))	count[1]++;
			else if(name.equals("ELSE"))	count[2]++;
			else if(name.equals("LOOP"))	count[3]++;
			else if(name.equals("ENDLP"))	count[4]++;
			else if(name.equals("ENDIF"))	count[5]++;
		}
	}

	static private class Data{
		static private List<String> TS = new ArrayList<String>();
		static private int No=0;

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

	static private class Variable {
		static private HashMap<String,HashMap<String,List<String>>> pro_map = new HashMap<String,HashMap<String,List<String>>>();
		static private ArrayList<String> procedure = new ArrayList<String>();
		static private int No=0;

		void clear() {
			pro_map.clear();
			No=0;
			procedure.clear();
		}

		int get_No(final String program, final String var) {
			if(pro_map.get(program).containsKey(var)) return Integer.parseInt(pro_map.get(program).get(var).get(0));
			if(pro_map.get("program").containsKey(var)) return Integer.parseInt(pro_map.get("program").get(var).get(0));
			return -1;
		}

		int get_min(final String program, final String var) {
			if(pro_map.get(program).containsKey(var)) return Integer.parseInt(pro_map.get(program).get(var).get(3));
			if(pro_map.get("program").containsKey(var)) return Integer.parseInt(pro_map.get("program").get(var).get(3));
			return -1;
		}

		void set_var(final String var,final String program, final List<String> type) {
			type.add(0,String.valueOf(No));
			pro_map.get(program).put(var, type);
			if(type.size() > 4) No = No + Integer.parseInt(type.get(4));
			else No++;

		}

		void add_program(final String program) {
			HashMap<String,List<String>> vars = new HashMap<String,List<String>>();
			pro_map.put(program,vars);
			procedure.add(program);
		}

		Boolean search_var(final String var,final String program) {
			if(pro_map.get(program).containsKey(var)) return true;
			return false;
		}

		Boolean search_var_all(final String var,final String program) {
			if(pro_map.get(program).containsKey(var) || pro_map.get("program").containsKey(var)) return true;
			return false;
		}

		String get_type(final String program,final String var) {
			if(pro_map.get(program).containsKey(var))	return pro_map.get(program).get(var).get(1);
			else if(pro_map.get("program").containsKey(var))	return pro_map.get("program").get(var).get(1);
			else return "null";
		}

		String get_array(final String program,final String var) {
			if(pro_map.get(program).containsKey(var))	return pro_map.get(program).get(var).get(2);
			else if(pro_map.get("program").containsKey(var))	return pro_map.get("program").get(var).get(2);
			else return "null";
		}

		int get_NOsize() {
			return No;
		}

		int get_proNo(final String program) {
			return procedure.indexOf(program);
		}

		Boolean search_proc(final String pro) {
			if(pro_map.containsKey(pro)) return true;
			else return false;
		}
	}

	class CodeError extends Exception{

		public CodeError(String string) {
			Data data = new Data();

			if(string.equals("Syntax")) System.err.println("Syntax error: line "+data.line());
			else if(string.equals("Semantic")) System.err.println("Semantic error: line "+data.line());
		}
	}

	public void run(final String inputFileName, final String outputFileName) {

		try {
			File file = new File(inputFileName);
			if(file.exists()) {

				Casl casl = new Casl();
				Variable Var = new Variable();
				Data data = new Data();
				FileReader filereader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String text;

				casl.clear();
				data.clear();
				Var.clear();

				while((text = bufferedReader.readLine()) != null) data.set_TS(text);
				program();

				bufferedReader.close();
				casl.output(outputFileName);
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
		Casl cas = new Casl();

		String program = "program";
		cas.add_program(program);

		Var.add_program(program);
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
			if(Var.search_var(data.name(),program) || name.contains(data.name()) || Var.search_proc(data.name())) throw new CodeError("Semantic");
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

			for(String x:name) {
				List<String> type = new ArrayList<String>();
				type.add(data.name());
				Var.set_var(x,program,type);
			}
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
		Casl casl = new Casl();
		String program;

		if(data.ID() != 16) throw new CodeError("Syntax");
		data.inc();
		if(data.ID() != 43) throw new CodeError("Syntax");
		program = data.name();
		if(Var.search_proc(program)) throw new CodeError("Semantic");
		casl.add_program(program);
		Var.add_program(program);
		data.inc();
		parameter(program);
		if(data.ID() != 37) throw new CodeError("Syntax");
		data.inc();

		return program;
	}

	private void parameter(final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		Casl casl = new Casl();

		if(data.ID() == 33) {
			casl.add_code(program,"\tPOP\tGR3\t;\n");
			List<String> paras = new ArrayList<String>();
			do{
				List<String> name = new ArrayList<String>();
				data.inc();
				para_names(name,program);
				if(data.ID() != 38) throw new CodeError("Syntax");
				data.inc();
				if(data.ID()!=3 && data.ID()!=4 && data.ID()!=11) throw new CodeError("Syntax");
				for(String x:name) {
					List<String> type = new ArrayList<String>();
					type.add(data.name());
					Var.set_var(x,program,type);
				}
				data.inc();
				paras.addAll(name);
			}while(data.ID() == 37);

			for(int i=paras.size()-1;i>=0;i--) {
				casl.add_code(program, "\tPOP\tGR1\t;\n");
				casl.add_code(program, "\tLD\tGR2, ="+Var.get_No(program, paras.get(i))+";\n");
				casl.add_code(program, "\tST\tGR1, VAR, GR2\t;\n");
			}
			casl.add_code(program, "\tPUSH\t0, GR3\t;\n");

			if(data.ID() != 34) throw new CodeError("Syntax");
			data.inc();
		}
	}

	private void para_names(final List<String> name,final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();

		while(true) {
			if(data.ID() != 43) throw new CodeError("Syntax");
			if(Var.search_var(data.name(),program) || name.contains(data.name()) || Var.search_proc(data.name())) throw new CodeError("Semantic");
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
		Casl casl = new Casl();

		String type = Var.get_type(program, var);
		int num = Var.get_No(program, var);
		casl.add_code(program, "\tPUSH\t"+num+";\n");

		if(!Var.search_var_all(var, program)) throw new CodeError("Semantic");
		if(data.ID() == 35) {
			data.inc();
			if(!formula(program).equals("integer")) throw new CodeError("Semantic");
			if(data.ID() != 36) throw new CodeError("Syntax");
			data.inc();
			type = Var.get_array(program, var);
			int min = Var.get_min(program, var);
			casl.add_code(program, "\tPOP\tGR1\t;\n");
			casl.add_code(program, "\tPOP\tGR2\t;\n");
			casl.add_code(program, "\tSUBA\tGR1, ="+min+"\t;\n");
			casl.add_code(program, "\tADDA\tGR2, GR1\t;\n");
			casl.add_code(program, "\tPUSH\t0, GR2\t;\n");
		}

		if(data.ID() != 40) throw new CodeError("Syntax");
		data.inc();
		if(!type.equals(formula(program))) throw new CodeError("Semantic");
		casl.add_code(program, "\tPOP\tGR1\t;\n");
		casl.add_code(program, "\tPOP\tGR2\t;\n");
		casl.add_code(program, "\tST\tGR1, VAR, GR2;\n");
	}

	private String formula(final String program) throws CodeError {
		Data data = new Data();
		Casl casl = new Casl();
		String type;

		if(data.ID()==30 || data.ID()==31) {
			int id = data.ID();
			data.inc();
			type = "integer";
			if(!term(program).equals(type)) throw new CodeError("Semantic");
			if(id == 31) {
				casl.add_code(program,"\tLD\tGR1, =0\t;\n");
				casl.add_code(program,"\tPOP\tGR2\t;\n");
				casl.add_code(program,"\tSUBA\tGR1, GR2\t;\n");
				casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
			}

		}else type = term(program);

		while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
			int id = data.ID();
			data.inc();
			if(!type.equals(term(program))) throw new CodeError("Semantic");

			casl.add_code(program,"\tPOP\tGR2\t;\n");
			casl.add_code(program,"\tPOP\tGR1\t;\n");
			if(id == 30) {
				if(!type.equals("integer")) throw new CodeError("Semantic");
				casl.add_code(program,"\tADDA\tGR1, GR2\t;\n");
			}else if(id == 31) {
				if(!type.equals("integer")) throw new CodeError("Semantic");
				casl.add_code(program,"\tSUBA\tGR1, GR2\t;\n");
			}else if(id == 15) {
				if(!type.equals("boolean")) throw new CodeError("Semantic");
				casl.add_code(program,"\tOR\tGR1, GR2\t;\n");
			}
			casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
		}

		if(data.ID()==24 || data.ID()==25 || data.ID()==26 || data.ID()==27 || data.ID()==28 || data.ID()==29) {
			int rel = data.ID();
			data.inc();
			if(data.ID()==30 || data.ID()==31) {
				int id = data.ID();
				data.inc();
				if(!term(program).equals(type)) throw new CodeError("Semantic");
				if(id == 31) {
					casl.add_code(program,"\tLD\tGR1, =0\t;\n");
					casl.add_code(program,"\tPOP\tGR2\t;\n");
					casl.add_code(program,"\tSUBA\tGR1, GR2\t;\n");
					casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
				}
			}else if(!term(program).equals(type)) throw new CodeError("Semantic");

			while(data.ID()==30 || data.ID()==31 || data.ID()==15) {
				int id = data.ID();
				if(!type.equals("integer")) throw new CodeError("Semantic");
				data.inc();
				if(!type.equals(term(program))) throw new CodeError("Semantic");
				casl.add_code(program,"\tPOP\tGR2\t;\n");
				casl.add_code(program,"\tPOP\tGR1\t;\n");
				if(id == 30) {
					if(!type.equals("integer")) throw new CodeError("Semantic");
					casl.add_code(program,"\tADDA\tGR1, GR2\t;\n");
				}else if(id == 31) {
					if(!type.equals("integer")) throw new CodeError("Semantic");
					casl.add_code(program,"\tSUBA\tGR1, GR2\t;\n");
				}else if(id == 15) {
					if(!type.equals("boolean")) throw new CodeError("Semantic");
					casl.add_code(program,"\tOR\tGR1, GR2\t;\n");
				}
				casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
			}

			casl.add_code(program,"\tPOP\tGR2\t;\n");
			casl.add_code(program,"\tPOP\tGR1\t;\n");
			casl.add_code(program,"\tCPA\tGR1, GR2\t;\n");

			String p_both = casl.get_both();
			String p_oper = casl.get_oper();
			casl.inc_count("BOTH");
			casl.inc_count("OPER");

			if(rel == 24) casl.add_code(program,"\tJZE\t"+p_oper+"\t;\n");	//=
			else if(rel == 25) casl.add_code(program,"\tJNZ\t"+p_oper+"\t;\n");	//<>
			else if(rel == 26 || rel == 28) casl.add_code(program,"\tJMI\t"+p_oper+"\t;\n");	//<,>=:false
			else if(rel == 29 || rel == 27) casl.add_code(program,"\tJPL\t"+p_oper+"\t;\n");	//>,/<=:false

			if(rel == 24||rel == 25||rel == 26||rel == 29) {
				casl.add_code(program,"\tLD\tGR1,=#FFFF\t;\n");
				casl.add_code(program,"\tJUMP\t"+p_both+"\t;\n");
				casl.add_code(program,p_oper+"\tLD\tGR1,=#0000\t;\n");
			}else if(rel == 27||rel == 28) {
				casl.add_code(program,"\tLD\tGR1,=#0000\t;\n");
				casl.add_code(program,"\tJUMP\t"+p_both+"\t;\n");
				casl.add_code(program,p_oper+"\tLD\tGR1,=#FFFF\t;\n");
			}
			casl.add_code(program,p_both+"\tPUSH\t0, GR1\t;\n");

			type = "boolean";
		}
		return type;
	}

	private String term(final String program) throws CodeError {
		Data data = new Data();
		Casl casl = new Casl();
		String type;

		type = factor(program);
		while(data.ID()==32 || data.ID()==5 || data.ID()==0 || data.ID()==12) {
			int id = data.ID();
			data.inc();
			if (!type.equals(factor(program))) throw new CodeError("Semantic");
			casl.add_code(program,"\tPOP\tGR2\t;\n");
			casl.add_code(program,"\tPOP\tGR1\t;\n");

			if(id == 32){
				if(!type.equals("integer")) throw new CodeError("Semantic");
				casl.add_code(program,"\tCALL\tMULT\t;\n");
				casl.add_code(program,"\tPUSH\t0, GR2\t;\n");
			}else if(id == 5) {
				if(!type.equals("integer")) throw new CodeError("Semantic");
				casl.add_code(program,"\tCALL\tDIV\t;\n");
				casl.add_code(program,"\tPUSH\t0, GR2\t;\n");
			}else if(id == 0) {
				if(!type.equals("boolean")) throw new CodeError("Semantic");
				casl.add_code(program,"\tAND\tGR1, GR2\t;\n");
				casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
			}else if(id == 12) {
				if(!type.equals("integer")) throw new CodeError("Semantic");
				casl.add_code(program,"\tCALL\tDIV\t;\n");
				casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
			}
		}
		return type;
	}

	private String factor(final String program) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		Casl casl = new Casl();
		String type;

		if(data.ID() == 13) {
			data.inc();
			type = factor(program);
			if(!type.equals("boolean")) throw new CodeError("Semantic");
			casl.add_code(program,"\tPOP\tGR1\t;\n");
			casl.add_code(program,"\tXOR\tGR1, =#FFFF\t;\n");
			casl.add_code(program,"\tPUSH\t0, GR1\t;\n");

		}else if(data.ID() == 33) {
			data.inc();
			type = formula(program);
			if(data.ID() != 34) throw new CodeError("Syntax");
			data.inc();

		}else if(data.ID()==44) {
			casl.add_code(program,"\tPUSH\t"+data.name() + "\t;\n");
			data.inc();
			type = "integer";

		}else if(data.ID()==45) {
			int length = data.name().length()-2;
			if(length >1) {
				casl.add_strings(data.name());
				casl.add_code(program,"\tPUSH\t"+length+"\t;\n");
				casl.add_code(program,"\tLAD\tGR1, CHAR"+casl.get_stringNo(data.name())+"\t;\n");
				type = "string";
			}else {
				casl.add_code(program,"\tLD\tGR1, =" + data.name() + "\t;\n");
				type = "char";
			}
			casl.add_code(program,"\tPUSH\t0, GR1\t;\n");
			data.inc();

		}else if(data.ID()==9 || data.ID()==20) {
			if(data.ID()==9)		casl.add_code(program,"\tPUSH\t#FFFF\t;\n");
			else if(data.ID()==20)	casl.add_code(program,"\tPUSH\t#0000\t;\n");
			data.inc();
			type = "boolean";

		}else if(data.ID() ==43) {
			type = Var.get_type(program,data.name());
			String name = data.name();
			data.inc();
			int num = Var.get_No(program, name);
			casl.add_code(program,"\tPUSH\t"+num+"\t;\n");

			if(data.ID() == 35) {
				type = Var.get_array(program,name);
				String i;
				data.inc();
				i = formula(program);
				if(data.ID() != 36) throw new CodeError("Syntax");
				if(!i.equals("integer")) throw new CodeError("Sematic");
				casl.add_code(program,"\tPOP\tGR1\t;\n");
				casl.add_code(program,"\tPOP\tGR2\t;\n");
				int min = Var.get_min(program, name);
				casl.add_code(program, "\tSUBA\tGR1, ="+min+"\t;\n");
				casl.add_code(program, "\tADDA\tGR2, GR1\t;\n");
				casl.add_code(program, "\tPUSH\t0, GR2\t;\n");
				data.inc();
			}
			casl.add_code(program,"\tPOP\tGR2\t;\n");
			casl.add_code(program,"\tLD\tGR1, VAR, GR2\t;\n");
			casl.add_code(program,"\tPUSH\t0, GR1\t;\n");

		}else throw new CodeError("Syntax");
		return type;

	}

	private void procedure(final String program,final String pro_name) throws CodeError {
		Data data = new Data();
		Variable Var = new Variable();
		Casl casl = new Casl();

		if(data.ID() == 33) {
			data.inc();
			d_formula_array(program);
			if(data.ID() != 34)throw new CodeError("Syntax");
			data.inc();
		}
		if(!Var.search_proc(pro_name)) throw new CodeError("Semantic");
		casl.add_code(program, "\tCALL\tPROG"+Var.get_proNo(pro_name)+"\t;\n");
	}

	private void d_formula_array(final String program) throws CodeError {
		Data data = new Data();

		formula(program);
		while(data.ID() == 41) {
			data.inc();
			formula(program);
		}
	}

	private void out_formula_array(final String program) throws CodeError {
		Data data = new Data();
		Casl casl = new Casl();
		String type;

		while(true) {
			type = formula(program);
			casl.add_code(program,"\tPOP\tGR2\t;\n");
			if(type.equals("string")) {
				casl.add_code(program,"\tPOP\tGR1\t;\n");
				casl.add_code(program,"\tCALL\tWRTSTR\t;\n");
			}else if(type.equals("integer")) casl.add_code(program,"\tCALL\tWRTINT\t;\n");
			else if(type.equals("char")) casl.add_code(program,"\tCALL\tWRTCH\t;\n");

			if(data.ID() != 41) break;
			data.inc();
		}
		casl.add_code(program,"\tCALL\tWRTLN\t;\n");
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
				out_formula_array(program);
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
		Casl casl = new Casl();
		String p_else = casl.get_else();
		String p_endif = casl.get_endif();
		casl.inc_count("ELSE");
		casl.inc_count("ENDIF");

		if(data.ID() != 10) throw new CodeError("Syntax");
		data.inc();
		if(!formula(program).equals("boolean")) throw new CodeError("Semantic");

		casl.add_code(program, "\tPOP\tGR1\t;\n");
		casl.add_code(program, "\tCPL\tGR1, =#FFFF\t;\n");
		casl.add_code(program, "\tJZE\t"+p_else+"\t;\n");
		if(data.ID() != 19) throw new CodeError("Syntax");
		data.inc();
		begin(program);

		if(data.ID() == 7) {
			data.inc();
			casl.add_code(program, "\tJUMP\t"+p_endif+"\t;\n");
			casl.add_code(program, p_else+"\tNOP\t;\n");
			begin(program);
			casl.add_code(program, p_endif+"\tNOP\t;\n");
		}else {
			casl.add_code(program, p_else+"\tNOP\t;\n");
		}

	}

	private void sent_while(final String program) throws CodeError {
		Data data = new Data();
		Casl casl = new Casl();
		String p_loop = casl.get_loop();
		String p_endlp = casl.get_endlp();
		casl.inc_count("LOOP");
		casl.inc_count("ENDLP");

		if(data.ID() != 22) throw new CodeError("Syntax");
		data.inc();
		casl.add_code(program, p_loop+"\tNOP\t;\n");
		if(!formula(program).equals("boolean")) throw new CodeError("Semantic");
		if(data.ID() != 6) throw new CodeError("Syntax");

		casl.add_code(program, "\tPOP\tGR1\t;\n");
		casl.add_code(program, "\tCPL\tGR1, =#FFFF\t;\n");
		casl.add_code(program, "\tJZE\t"+p_endlp+"\t;\n");
		data.inc();
		begin(program);
		casl.add_code(program, "\tJUMP\t"+p_loop+"\t;\n");
		casl.add_code(program, p_endlp+"\tNOP\t;\n");
	}

}