# Guia Definitivo de Diretrizes de Desenvolvimento Android: Do Setup à Excelência Técnica

## 1. Fase de Preparação: Ambiente e Fundamentos da Plataforma

A estabilidade do ciclo de vida de um software mobile é indissociável da padronização rigorosa do ambiente. Como avaliador, reforço que a conformidade técnica começa na configuração: um ambiente idêntico entre desenvolvedores elimina os "bugs de infraestrutura" e garante que a aplicação se comporte de forma previsível em diferentes níveis de API. Uma vez que o ambiente esteja isolado e as ferramentas configuradas, a transição para a estruturação lógica do projeto torna-se o próximo passo crítico para a manutenibilidade.

### Configuração Estrita do Android Studio e Tooling

Utilize exclusivamente o Android Studio. Ao iniciar, selecione o modelo Empty Activity para garantir uma base limpa e compatível com as tecnologias modernas.

*   **Níveis de API:** Configure o Minimum SDK de acordo com a complexidade do projeto. Utilize a API 21 (Android 5.0 - Lollipop) para projetos fundamentais como o Greeting Card. Eleve para a API 24 (Android 7.0 - Nougat) em projetos que exijam recursos mais robustos, como o Happy Birthday.
*   **Fluxo de Trabalho:** Utilize sempre o modo "Split" no editor para visualizar simultaneamente o código e o design. Antes de qualquer commit, execute a ação "Optimize Imports" (via Find Action) para manter o código limpo e livre de referências não utilizadas que geram o temido "red text".

### Arquitetura do Sistema: O Papel do Isolamento

Embora o Kernel Linux gerencie o hardware e a Máquina Virtual Dalvik (ou ART) processe o bytecode, o conceito fundamental de segurança é o Sandbox. Cada aplicativo Android é executado em seu próprio ambiente isolado, garantindo que falhas em um processo não comprometam a integridade do sistema ou de outros apps.

*   **Gradle:** Automatize o build e gerencie dependências através deste sistema; negligenciar as versões do Gradle é abrir as portas para instabilidades de compilação.
*   **Android Manifest:** Este é o cérebro estrutural do app. É obrigatório declarar todos os componentes, permissões e metadados no AndroidManifest.xml, sob pena de falha catastrófica na execução.

### Organização de Arquivos e Pastas

Mantenha a hierarquia oficial para garantir que o compilador localize os recursos:

*   **java/kotlin:** Lógica de negócio e controladores organizada em pacotes (ex: com.example.greetingcard).
*   **res (Resources):** Recursos não compilados. Atenção especial à pasta layout (para XML tradicional) e drawable (para imagens).
*   **manifests:** Contém o arquivo de manifesto único da aplicação.

## 2. Fase de Definição Arquitetural: Padrão MVVM e Gestão de Dados

A separação de responsabilidades via MVVM (Model-View-ViewModel) não é uma sugestão, mas a estratégia central para evitar falhas durante mudanças de configuração, como a rotação da tela. Como engenheiro sênior, exijo que a lógica de UI e a lógica de negócios nunca residam no mesmo componente. Implementar essa arquitetura é o que permite ao código sobreviver à natureza efêmera do sistema operacional Android.

### O Modelo MVVM e a Eliminação de Acoplamento

*   **Model:** Gerencia a fonte de dados e a persistência.
*   **View:** (Activity/Fragment/Composable) Deve ser "burra", limitando-se a exibir dados e capturar eventos.
*   **ViewModel:** O componente de excelência que permite aos dados sobreviverem a mudanças de configuração. Ele retém o estado enquanto a Activity é destruída e recriada pelo sistema.

### Insight Técnico: Vinculação de Dados (Data Binding)

O padrão de excelência técnica exige o uso de ViewModel e LiveData (ou StateFlow) para eliminar a necessidade de gerenciadores de cliques (click handlers) nos controladores da interface (Activities/Fragments). Ao configurar observadores (observers) que atualizam a UI automaticamente, você reduz o código boilerplate e centraliza a lógica de eventos, resultando em controladores muito mais enxutos e fáceis de testar.

## 3. Fase de Ciclo de Vida e Gerenciamento de Estados

Componentes mobile são efêmeros e o sistema operacional pode encerrá-los para recuperar memória a qualquer momento. Dominar o ciclo de vida é o requisito básico para evitar vazamentos de memória e crashes. Uma vez compreendido que os componentes podem "morrer" a qualquer instante, o foco deve se voltar para como eles se comunicam de forma segura entre si.

### Ciclo de Vida: Activities e Fragments

*   **Activity:** O método onCreate() é o ponto de entrada análogo ao main() do Kotlin. É aqui que a inicialização lógica e a definição da UI via setContent devem ocorrer.
*   **Gerenciamento de Eventos:** Gerencie rigorosamente os estados para garantir que o app responda corretamente a interrupções (como chamadas telefônicas).
*   **Navegação e Fragments:** Para múltiplos fragmentos, é obrigatório o uso do Navigation Graph para gerenciar transições e garantir o uso de Safe Args, evitando erros de tipo na passagem de parâmetros.

### Comunicação e Performance

*   **Intents:** Utilize as explícitas para componentes internos e implícitas para ações do sistema (via Intent Filters).
*   **Parcelable vs Serializable:** Rejeite o Serializable em favor do Parcelable. O Parcelable é otimizado especificamente para o Android, oferecendo desempenho superior em transferências IPC (Inter-Process Communication).

## 4. Fase de Implementação de UI: Jetpack Compose

A transição do modelo imperativo (XML) para o declarativo (Compose) acelera drasticamente o ciclo de desenvolvimento. No Compose, a interface é uma função do estado. Quando o estado muda, ocorre a Recomposition — o framework reexecuta as funções combináveis para atualizar a tela.

### Regras de Ouro de Nomenclatura e Pureza

As funções @Composable devem seguir restrições estritas para manter a integridade do código:

*   **Nomenclatura:** Use PascalCase e sempre Substantivos (ex: GreetingCard, BirthdayCardPreview).
*   **Proibições:** É estritamente proibido o uso de verbos (ex: DrawTextField), preposições nominais (ex: TextFieldWithLink), adjetivos isolados ou advérbios.
*   **Pureza:** Funções @Composable não podem retornar nenhum valor (devem retornar Unit) e devem ser puras, ou seja, não devem gerar efeitos colaterais durante a recomposição.

### Gestão de Estado e Unidades de Medida

*   **State:** Diferencie remember (memória durante composição) de rememberSaveable (sobrevive à recriação da Activity).
*   **Unidades:** Use SP (pixels escalonáveis) exclusivamente para fontes para respeitar as configurações de acessibilidade do usuário. Use DP (densidade de pixels) para todas as medidas de espaçamento, padding e dimensões de layout.
*   **Layouts:** Organize elementos com Column (vertical), Row (horizontal) ou Box (sobreposição). Para listas, use LazyColumn para eficiência de memória.

### Recursos e Acessibilidade (res)

Ao importar imagens para res/drawable (como o recurso androidparty ou R.drawable.graphic):

1.  Configure a densidade como "No Density" para imagens decorativas, economizando memória e evitando escalas desnecessárias.
2.  Defina o parâmetro contentDescription como null em imagens puramente estéticas. Isso instrui o TalkBack a ignorar o elemento, melhorando a experiência de acessibilidade para usuários que dependem de leitores de tela.

## 5. Fase de Navegação e Fluxo do Usuário

Um fluxo centralizado é o que garante a integridade do grafo da aplicação e uma experiência de usuário fluida. O uso de mecanismos manuais de fragmentos é desencorajado em favor de uma arquitetura baseada em destinos claros e rotas definidas.

*   **Pilha de Navegação:** O NavController deve gerenciar a lógica de navegação, enquanto o NavHost serve como contêiner.
*   **Navigation Graph:** Utilize-o para simplificar o tratamento dos botões "Up" e "Back" do sistema, garantindo que o usuário nunca fique "preso" em um destino ou sofra com comportamentos inesperados ao voltar telas.

## 6. Fase de Documentação, Testes e Entrega Final

A aderência total às restrições técnicas é o critério de desempate para a excelência. Um aplicativo que ignora a ofuscação ou que falha no isolamento de processos não atinge o padrão sênior.

### Segurança e Preparação

*   **ProGuard:** Aplique as regras de ProGuard para reduzir o tamanho do pacote e ofuscar o código contra engenharia reversa.
*   **Pacotes:** Gere o AAB (Android App Bundle) para publicação na Google Play, permitindo que a loja gere APKs otimizados para a arquitetura específica de cada dispositivo. Reserve o APK apenas para testes manuais e instalação direta.

## Checklist de Conformidade Técnica

*   [ ] **Zero Red Text:** O código foi limpo com "Optimize Imports" e todas as classes foram importadas corretamente?
*   [ ] **Nomenclatura Compose:** Todas as funções @Composable usam PascalCase e substantivos? (Sem verbos como Draw ou Show).
*   [ ] **Acessibilidade:** Imagens decorativas estão com contentDescription = null?
*   [ ] **Recursos:** Imagens de fundo estão configuradas como No Density no diretório drawable?
*   [ ] **Unidades de Medida:** Foi usado SP para textos e DP para paddings/espaçamentos?
*   [ ] **Previews:** As funções @Preview fornecem valores padrão para todos os parâmetros para permitir a renderização no Android Studio?
*   [ ] **API Level:** O Minimum SDK reflete a exigência do projeto (API 21 ou API 24)?
*   [ ] **Arquitetura:** A lógica de cliques foi removida da Activity e movida para o ViewModel com vinculação de dados?
