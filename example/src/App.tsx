/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { useRef, useState } from 'react';
import { Button, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';

import SimCardsManagerModule from 'react-native-sim-cards-manager';

interface ILog {
  command: string;
  result: any;
}

export default function App() {
  const [logs, setLogs] = useState<Array<ILog>>([]);
  const [confirmationCode, setConfirmationCode] = useState('');
  const scrollViewRef = useRef<any>();

  const callBridge = async (command: string, method: any, params?: any) => {
    let result;
    try {
      result = params ? await method(params) : await method();
    } catch (e) {
      result = e;
    } finally {
      setLogs([
        ...logs,
        {
          command,
          result: JSON.stringify(result, null, 5),
        },
      ]);
    }
  };

  const getSimCards = () => callBridge('getSimCards', SimCardsManagerModule.getSimCards);

  const isEsimSupported = () =>
    callBridge('isEsimSupported', SimCardsManagerModule.isEsimSupported);

  const setupEsim = async () =>
    callBridge(`setupEsim (${confirmationCode})`, SimCardsManagerModule.setupEsim, {
      confirmationCode,
      address: '',
    });

  return (
    <SafeAreaView>
      <View style={styles.mainView}>
        <View style={styles.header}>
          <Text style={styles.headerText}>RN Sim Cards Manager</Text>
        </View>
        <ScrollView
          style={styles.logsContainer}
          contentContainerStyle={{ paddingHorizontal: 4 }}
          ref={scrollViewRef}
          onContentSizeChange={() => scrollViewRef?.current?.scrollToEnd({ animated: true })}
        >
          {logs.map((log, index) => (
            <>
              <Text style={styles.logText} key={index}>
                {log.command} :
              </Text>
              <Text style={styles.logTextResult} key={`result-${index}`}>
                {`${log.result}`}
              </Text>
            </>
          ))}
        </ScrollView>
        <View style={styles.button}>
          <Button title={'Get sim cards'} onPress={getSimCards}></Button>
        </View>
        <View style={styles.button}>
          <Button title={'Is Esim supported'} onPress={isEsimSupported}></Button>
        </View>
        <View style={styles.activateEsimContainer}>
          <TextInput style={styles.textInput} onChangeText={setConfirmationCode}></TextInput>
          <View style={styles.button}>
            <Button title={'Activate Esim'} onPress={setupEsim}></Button>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  header: {
    paddingVertical: 20,
    alignItems: 'center',
  },
  headerText: {
    fontSize: 24,
    fontWeight: '600',
  },
  button: {
    paddingVertical: 10,
  },
  activateEsimContainer: {
    paddingVertical: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  mainView: {
    paddingHorizontal: 18,
  },
  textInput: {
    marginRight: 18,
    backgroundColor: 'lightgrey',
    flex: 1,
  },
  logsContainer: {
    height: 300,
    backgroundColor: 'black',
    borderRadius: 5,
  },
  logText: {
    color: 'lightgrey',
  },
  logTextResult: {
    color: 'lightgrey',
    marginLeft: 20,
    marginBottom: 5,
  },
});
