# Avanade Decola 2025 - Board de Tarefas API

## Principais Tecnologias
- Java 17: foi utilizada a versão 17 do Java, pois tem compatibilidade com o build do Maven;
- Spring Boot 3 (3.4.3): a versão mais recente LTS do Spring Boot;
- Spring Web
- Spring Data JPA: facilita a integração com bancos de dados SQL;
- Spring Security: para a autenticação e segurança da API;
- OpenAPI (Swagger): criação da documentação da API;
- MySQL

## Modelagem do Banco de Dados

O banco de dados possui as seguintes tabelas:

```mermaid
classDiagram
    class Enderecos {
        +cep INT UNSIGNED (PK)
        +logradouro VARCHAR(50)
        +bairro VARCHAR(50)
        +cidade VARCHAR(20)
        +tipo ENUM('Cliente', 'Funcionario', 'Fornecedor', 'Transportadora')
    }

    class Clientes {
        +id INT (PK)
        +nome VARCHAR(150)
        +email VARCHAR(100)
        +telefone BIGINT UNSIGNED
        +cep_cliente INT UNSIGNED (FK)
        +n_residencial INT UNSIGNED
    }

    class Cargos {
        +id INT (PK)
        +descricao VARCHAR(200)
    }

    class Funcionarios {
        +id INT (PK)
        +cpf BIGINT UNSIGNED
        +nome VARCHAR(150)
        +id_cargo INT (FK)
        +telefone BIGINT UNSIGNED
        +cep_funcionario INT UNSIGNED (FK)
        +n_residencial INT UNSIGNED
    }

    Enderecos "1" -- "0..*" Clientes : cep_cliente
    Enderecos "1" -- "0..*" Funcionarios : cep_funcionario
    Enderecos "1" -- "0..*" Fornecedores : cep_fornecedor
    Enderecos "1" -- "0..*" Transportadoras : cep_transportadora
```

## Documentação da API (Deploy com Swagger)

...