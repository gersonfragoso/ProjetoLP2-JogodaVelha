package cliente;

import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

public class JanelaSelecionarServidor extends JFrame implements ActionListener {
    private Jogador jogador;
    private String host;
    private int porta;





    public JanelaSelecionarServidor(Jogador jogador) throws IOException, JSONException {
        Scanner in = new Scanner(System.in);

        JFrame janela = new JFrame("Conectar Servidor");
        janela.setVisible(true);
        janela.setSize(400, 200);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null);
        janela.setLayout(null);


        //adicionando botão conectar
        JButton conectar = new JButton("conectar");

        conectar.setBounds(280, 120, 90, 30);
        janela.add(conectar);


        //Inserir IP,HOST E APELIDO
        JTextField getHost = new JTextField("");
        getHost.setBounds(80, 90, 250, 20);
        janela.add(getHost);
        getHost.setVisible(true);

        JTextField NomeJogador = new JTextField("");

        NomeJogador.setBounds(80, 60, 250, 20);
        janela.add(NomeJogador);
        NomeJogador.setVisible(true);

        JTextField getPorta = new JTextField("");

        getPorta.setBounds(80, 30, 250, 20);
        janela.add(getPorta);
        getPorta.setVisible(true);

        //LABELL PARA COLOCAR INFORMAÇÃO

        JLabel Host = new JLabel("HOST:");
        Host.setBounds(30, 20, 40, 40);
        Host.setFont(new Font("Arial", Font.PLAIN, 12));
        janela.add(Host);

        JLabel Apelido = new JLabel("NOME:");
        Apelido.setBounds(30, 50, 40, 40);
        Apelido.setFont(new Font("Arial", Font.PLAIN, 12));
        janela.add(Apelido);

        JLabel Porta = new JLabel("PORTA:");
        Porta.setBounds(30, 80, 40, 40);
        Porta.setFont(new Font("Arial", Font.PLAIN, 11));
        janela.add(Porta);
        conectar.addActionListener(this);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Jogador jogador = new Jogador();
        try {
            jogador.conectarServidor();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


    }

    public static void main(String[] args) throws IOException {


    }
}




