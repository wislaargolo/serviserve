package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Telefone;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelefoneDAO extends AbstractDAOImpl<Telefone, Long> {


    @Override
    public Telefone salvar(Connection conn, Telefone telefone) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_funcionario, telefone_funcionario) VALUES (?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, telefone.getFuncionario().getId());
            stmt.setString(2, telefone.getTelefone());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do funcionário falhou, nenhuma linha afetada.");
            }
        }

        return telefone;
    }

    @Override
    public void atualizar(Connection conn, Telefone... telefones) throws SQLException {

        if (telefones.length != 2) {
            throw new IllegalArgumentException("ERRO >> São necessários exatamente dois telefones para realizar a atualização.");
        }

        Telefone telefoneAntigo = telefones[0];
        Telefone telefoneNovo = telefones[1];

        String sql = String.format(
                "UPDATE %s SET telefone_funcionario = ? WHERE telefone_funcionario = ? AND id_funcionario = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefoneNovo.getTelefone());
            stmt.setString(2, telefoneAntigo.getTelefone());
            stmt.setLong(3, telefoneAntigo.getFuncionario().getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    @Override
    protected Telefone mapearResultado(ResultSet rs) throws SQLException {
        Telefone telefone = new Telefone();
        telefone.getFuncionario().setId(rs.getLong("id_funcionario"));
        telefone.setTelefone(rs.getString("telefone_funcionario"));
        return telefone;
    }

    @Override
    public String getNomeTabela() {
        return "telefones";
    }

    public List<Telefone> buscarPorFuncionarioId(Long id) throws SQLException {
        List<Telefone> resultados = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s WHERE id_funcionario = %s", getNomeTabela(), id);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapearResultado(rs));
            }
        }
        return resultados;
    }

    public void deletar(Connection conn, String telefone, Long funcionarioId) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE id_funcionario = ? AND telefone_funcionario = ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, funcionarioId);
            stmt.setString(2, telefone);
            stmt.executeUpdate();
        }
    }

    public boolean existeTelefone(Connection conn, Telefone telefone) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE id_funcionario = ? AND telefone_funcionario = ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, telefone.getFuncionario().getId());
            stmt.setObject(2, telefone.getTelefone());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }
}