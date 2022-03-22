/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { useEffect, useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';

import SimCardsManagerModule from 'react-native-sim-cards-manager';

export default function App() {
  const [eSimSupported, setESimSupported] = useState<boolean>(false);

  useEffect((): void => {
    SimCardsManagerModule.isEsimSupported().then(setESimSupported);
  }, []);

  const eSimSupportedLabel = eSimSupported ? 'Esim not supported' : 'Esim supported';
  return (
    <SafeAreaView>
      <ScrollView contentInsetAdjustmentBehavior="automatic">
        <View style={styles.header}>
          <Text style={styles.headerText}>RN Sim Cards Manager</Text>
        </View>
        <View>
          <Text>eSimSupported : {eSimSupportedLabel}</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  header: {
    paddingHorizontal: 10,
    paddingVertical: 20,
    alignItems: 'center',
  },
  headerText: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});
