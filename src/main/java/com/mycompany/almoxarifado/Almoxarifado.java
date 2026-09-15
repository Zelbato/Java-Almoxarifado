package com.mycompany.almoxarifado;

import com.mycompany.almoxarifado.config.DatabaseConnection;
import com.mycompany.almoxarifado.config.MigrationConfig;
import com.mycompany.almoxarifado.Dto.SetorDTO;
import com.mycompany.almoxarifado.Dao.SetorDAO;
import com.mycompany.almoxarifado.View.LoginView;

import java.sql.Connection;

/**
 *
 * @author heito
 */
public class Almoxarifado {

    public static void main(String[] args) {

        MigrationConfig.migrate();

        System.out.println("Sistema Iniciado");

        /*
        try {
            SetorDTO setor = new SetorDTO();

            setor.setNome("Produção");
            setor.setDescricao("Setor responsavel pela prodção");
            setor.setAtivo(true);

            SetorDAO setorDAO = new SetorDAO();

            setorDAO.cadastrar(setor);
            System.out.println("Setor cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
        java.awt.EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }
}
