package sisaku;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EdgeServer {
	final static int ID = 3000;;//エッジサーバのID
	String message;

	Udp udp;

	EdgeServer() throws IOException{
		udp = new Udp(ID);//UDPインスタンスにID付与
		udp.makeMulticastSocket();//ソケット生成
		udp.startListener();//受信
	}

	void receiveData() throws IOException{//受信メソッド
		byte[] rcvData = udp.lisner.getData();//受信データ

		if(rcvData != null) {
			String str = new String(rcvData);//byte型から文字に変換
			System.out.println("(エッジサーバ受信データ) "+str);
			String[] eachData = str.split(" ", 0);//受信データの分割

			for (int i = 0 ; i < eachData.length ; i++){
			      System.out.println(i + "番目の要素 = :" + eachData[i]);
			}

			int dport = Integer.parseInt(eachData[1]);
			judgmentData(dport,eachData[3]);//eachData[3]=discovery

			if(!(eachData[9].equals("normal")) && !(eachData[9].equals("Decline"))) {//プロトコルへ
				if(eachData[9].equals("Yes")) {
					System.out.println(eachData[9]);
					message = "ProvisionalRequest";
					udp.sendMsgsFromServer(dport,message);
					udp.lisner.resetData();
				}

				if(eachData[9].equals("ProvisionalReply")) {
					message = "MainRequest";
					udp.sendMsgsFromServer(dport,message);
					udp.lisner.resetData();
				}
			}


			//System.out.println(eachData[9]);//メッセージ表示

			try {//ファイルへの書き込み
				FileWriter fw = new FileWriter("/Users/TKLab/Desktop/data.txt",true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(str);
				bw.newLine();//改行
				bw.flush();
				bw.close();//ファイル閉鎖
			}catch(IOException e) {
				System.out.println("エラー");
			}

			udp.lisner.resetData();//バッファの中のデータをリセット
		}

	}

	void  judgmentData(int dport,String peaple){
		int j = Integer.parseInt(peaple);

		if(j != 0) {
			message = "T";
			udp.sendMsgsFromServer(dport,message);
		}
		else {
			message = "F";
			udp.sendMsgsFromServer(dport,message);
		}
	}

}
