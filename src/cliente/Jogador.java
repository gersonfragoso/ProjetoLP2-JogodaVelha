package cliente;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Jogador{
    private String ip;
    private String apelido;
    private String host = "192.168.0.100";
    private int porta;
    public boolean isHostSala;
    private boolean conectadoServidor;
    Socket servidor;
    Socket sala;
    Sala hSala;

    public boolean conectarServidor()throws UnknownHostException,IOException {
        boolean sucesso = true;
        this.host = host;
        this.porta  = porta;
        try{
            servidor = new Socket(host,porta);
            this.ip = servidor.getLocalAddress().toString();
            conectadoServidor = true;
        }catch (Exception e){
            sucesso = false;
        }
        return sucesso;

    }

    public String getIp(){

        return this.ip;
    }
    public void entrarSala(String ipSala) throws IOException, JSONException {
        JSONObject requisicao = new JSONObject();
        requisicao.put("tipo","entrarSala");
        requisicao.put("ipHost",ipSala);

        PrintStream out = new PrintStream(servidor.getOutputStream());
        out.println(requisicao);

        Scanner in = new Scanner(servidor.getInputStream());
        JSONObject resposta = new JSONObject(in.hasNextLine());

        if(resposta.getBoolean("aceito")){
            sala= new Socket(ipSala,12340);
            JogadorAtivo ja = new JogadorAtivo(sala,this);
            new Thread(ja).start();
        }else{
            JOptionPane.showMessageDialog(new JFrame(),
            "Sala Cheia!",
            "Inane error",
            JOptionPane.ERROR_MESSAGE);
        }

    }
    public JSONObject getListaSalas() throws IOException, JSONException {
        JSONObject requisicao = new JSONObject();
        requisicao.put("tipo","getListaSalas");

        PrintStream out = new PrintStream(servidor.getOutputStream());
        out.println(requisicao);

        Scanner in = new Scanner(servidor.getInputStream());

        JSONObject lista = new JSONObject(in.nextLine());
        return lista;
    }
    public void criarSala(String nomeSala) throws IOException, JSONException {
        JSONObject requisicao = new JSONObject();
        requisicao.put("tipo","criarSala");
        requisicao.put("nomeSala",nomeSala);
        requisicao.put("maxJogadores",2);

        PrintStream out = new PrintStream(servidor.getOutputStream());
        out.println(requisicao);

        hSala = new Sala(nomeSala);
        new Thread(hSala).start();

        sala = new Socket(hSala.getIpHost(),12340);
        JogadorAtivo ja = new JogadorAtivo(sala,this);
        new Thread(ja).start();
    }
    public void desconectar()throws IOException{
        sala.close();

    }
    public void sairSala()throws IOException, Throwable{
        if(this.isHostSala){
            destruirSala();
        }else{
            JSONObject requisicao = new JSONObject();
            requisicao.put("tipo","sairSala");
            PrintStream out = new PrintStream(servidor.getOutputStream());
            out.println(requisicao);
            desconectar();
        }
    }
    public void destruirSala() throws IOException, Throwable{
        JSONObject requisicao = new JSONObject();
        requisicao.put("tipo","destruirSala");
            PrintStream out = new PrintStream(servidor.getOutputStream());
        out.println(requisicao);
        hSala.fecharSala();


        };
    public String getApelido(String jogadorApelido){
        return this.apelido;

    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public static void main(String[] args) throws IOException, JSONException {
        Jogador jogador = new Jogador();
        jogador.conectadoServidor = false;

        //exibe janela solicitando ip e a porta do servidor e se conectar
        JFrame janela = new JanelaSelecionarServidor(jogador);
        janela.setTitle("jogo da velha");
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);

        new JanelaSelecionarServidor(jogador);

        while (jogador.conectadoServidor==true) {
                System.out.println("conectado");
        }



    }

}


